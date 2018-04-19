package com.ppr.ppr;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Hao on 4/19/18.
 */
@Service
public interface PPRService {

    void readData(String pageURL, String pageEdges, String desiredCategory);
    void calculate(int iteration, boolean ppr);
    String showResult(String category);
    List<String> showTopRank(int num);
    List<String> showDiff(PRService pr, String category);
    Map<String, PPRNode> getAllNodes();
    String topId();
    List<String> showList(String category);
}
