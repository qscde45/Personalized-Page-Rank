package com.ppr.ppr;

/**
 * Created by Hao on 4/19/18.
 */
/**
 * Created by Hao on 4/17/18.
 */
public class PPRNode implements Comparable<PPRNode> {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String category;
    private double rank;

    public PPRNode(String id, String category, double rank) {
        this.id = id;
        this.category = category;
        this.rank = rank;
    }

    @Override
    public int compareTo(PPRNode another){
        return Double.compare(this.getRank(), another.getRank());
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }



}

