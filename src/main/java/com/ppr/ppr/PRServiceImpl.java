package com.ppr.ppr;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Hao on 4/19/18.
 */
@Service
public class PRServiceImpl implements PRService{

    private Map<String, List<String>> graph;
    private Map<String, Integer> outDegree;

    @Override
    public Map<String, PPRNode> getAllNodes() {
        return allNodes;
    }

    //store the rank of every node
    private Map<String, PPRNode> allNodes;

    private Set<String> outNodes; //Nodes that have outgoing edges, used to determine sink nodes.
    private Set<String> sinkNodes;
    private Set<String> categories;
    //private boolean pr;

    int N; //# of nodes

    double lambda;

    public PRServiceImpl() {
        graph = new HashMap<>();
        allNodes = new HashMap<>();
        outDegree = new HashMap<>();
        outNodes = new HashSet<>();
        categories = new HashSet<>();
        //pr = false;
        sinkNodes = new HashSet<>();
        lambda = 0.85;
    }


    public void readData(String pageURL, String pageEdges, String desiredCategory) {
        try {
            Scanner scanner = new Scanner(new File(pageURL));

            N = Integer.parseInt(scanner.nextLine());
            //read page
            while (scanner.hasNextLine()) {
                String[] s = scanner.nextLine().trim().split(" ");
                if (s.length < 2)
                    System.out.println(Arrays.toString(s));
                String id = s[0];
                String url = s[1];

                String[] urls = s[1].split("/");
                String category = urls.length >= 4 ? urls[3] : "";
                allNodes.put(id, new PPRNode(id, category, url, 1.0 / N));

                if (category.equals(desiredCategory))
                    categories.add(id);
            }

            //read edges
            //scanner.close();
            scanner = new Scanner(new File(pageEdges));
            while (scanner.hasNextLine()) {
                String[] s = scanner.nextLine().split(" ");
                String uNode = s[0];
                String vNode = s[1];

                outDegree.put(uNode, outDegree.getOrDefault(uNode, 0) + 1);

                outNodes.add(uNode);

                List<String> inNodes = graph.get(vNode);
                if (inNodes == null)
                    inNodes = new ArrayList<>();
                inNodes.add(uNode);
                graph.put(vNode, inNodes);
            }

            for (Map.Entry<String, PPRNode> entry : allNodes.entrySet()){
                String node = entry.getKey();
                if (!outNodes.contains(node))
                    sinkNodes.add(node);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, PPRNode> copyMap(Map<String, PPRNode> original){
        Map<String, PPRNode> newMap = new HashMap<>();
        for (Map.Entry<String, PPRNode> entry : original.entrySet()) {
            String s = entry.getKey();
            String category = entry.getValue().getCategory();
            String url = entry.getValue().getUrl();
            double rank = entry.getValue().getRank();
            newMap.put(s, new PPRNode(s, category, url, rank));
        }
        return newMap;
    }

    public void calculate(int iteration, boolean ppr) {
        int i = 0;
        int Ndesired = categories.size();
        while(i < iteration) {
            Map<String, PPRNode> tmp = copyMap(allNodes);
            boolean converge = true;
            for (Map.Entry<String, List<String>> entry : graph.entrySet()) {

                String curr = entry.getKey();
                double oldRank = allNodes.get(curr).getRank();
                double hopToDesired = categories.contains(curr) ? 1.0 * (1 - lambda) / Ndesired : 0;
                if (!ppr)
                    hopToDesired = 1.0 * (1 - lambda) / N;

                //calculate sum of the pr from innodes
                double sumIn = 0;
                for (String inNode : entry.getValue()){
                    sumIn += allNodes.get(inNode).getRank() / outDegree.get(inNode);
                }
                sumIn = lambda * sumIn;

                //calculate sum of the pr from sinknodes

                double sumSink = 0;
                for (String sink : sinkNodes){
                    sumSink += allNodes.get(sink).getRank();
                }
                sumSink = sumSink * lambda / N;
                double newRank = hopToDesired + sumIn + sumSink;

                tmp.put(curr, new PPRNode(curr, allNodes.get(curr).getCategory(), allNodes.get(curr).getUrl(), newRank));

                if (Math.abs(newRank - oldRank) > 0.00001)
                    converge = false;
            }
            if (converge) {
                //System.out.println("PageRank converges at the " + i + "th iteration");
                break;
            }
            allNodes = tmp;
            i++;
        }
    }

    public String showResult(String category) {
        //List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("\nThe nodes in " + category + " category:\n");
        //System.out.println();
        for (String s : categories) {
            sb.append("The rank of " + s + " is " + allNodes.get(s).getRank());
        }
        return sb.toString();
    }

    public List<String> showList(String category){
        List<String> list = new ArrayList<>();
        list.add("\nThe nodes in " + category + " category:\n");
        //System.out.println();
        for (String s : categories) {
            list.add("The rank of " + s + " is " + allNodes.get(s).getRank());
        }
        return list;
    }

    public List<String> showTopRank(int num) {
//        if (num >= allNodes.size()){
//            System.out.println("The input size is greater than the number of nodes");
//            return;
//        }

        List<PPRNode> list = new ArrayList<>();

        for (Map.Entry<String, PPRNode> entry : allNodes.entrySet())
            list.add(entry.getValue());
        Collections.sort(list);
        List<String> res = new ArrayList<>();
        res.add("Top " + num + " ranked pages after Regular PR");
        for (int i = 1; i <= num; i++) {
            int size = list.size();
            PPRNode node = list.get(size - i);
            res.add(i + "th node: " + node.getId() + "  Category: " + node.getCategory() +
                    "  URL: "+ node.getUrl() + "  Rank: "
                    + String.format("%.7f", node.getRank()));
        }
        return res;
    }

    public String topId(){
        //showTopRank(10);
        return allNodes.get("11").getCategory();
    }

    public List<String> showDiff(PPRService pr, String category) {
        List<String> list = new ArrayList<>();
        //StringBuilder sb = new StringBuilder();
        list.add("\nThe nodes in category: " + category + "\n");
        for (String s : categories) {
            list.add("Page ID: " + s + ". Its rank after PPR is " + allNodes.get(s).getRank() +
                    ". Its rank after regular PR is " + pr.getAllNodes().get(s).getRank() + "\n");
        }
        return list;
    }

    public void showAllRanks(){
        //System.out.println("PPR ");
        double sum = 0;
        for (Map.Entry<String, PPRNode> entry : allNodes.entrySet())
            sum += entry.getValue().getRank();
        System.out.println(sum);
    }
}
