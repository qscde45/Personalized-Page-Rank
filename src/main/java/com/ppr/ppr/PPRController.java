package com.ppr.ppr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Hao on 4/19/18.
 */
@Controller
@RequestMapping("/demo")
public class PPRController {

    private PPRService pprService;
    private PRService prService;

    @Autowired
    public PPRController(PPRService pprService, PRService prService) {
        this.pprService = pprService;
        this.prService = prService;
    }

    @RequestMapping("/home")
    public String getHomePage(){
        return "homepage";
    }

//    @GetMapping("/ppr")
//    @ResponseBody
//    public String calculate(){
//        pprService.readData("pageURL", "pageEdge", "faculty");
//        pprService.calculate(10);
//        //return pprService.topId();
//        return pprService.showResult("faculty");
//    }



    @GetMapping("/diff")
    public @ResponseBody List<String> calculateList(@RequestParam("category") String category){
        pprService.readData("pageURL", "pageEdge", category);
        pprService.calculate(100, true);
        prService.readData("pageURL", "pageEdge", category);
        prService.calculate(100, false);
        return pprService.showDiff(prService, category);
    }

//    @GetMapping("/pprtop")
//    public @ResponseBody List<String> pprtop(@RequestParam("num") int num){
//        return pprService.showTopRank(num);
//    }
//
//    @GetMapping("/prtop")
//    public @ResponseBody List<String> prtop(@RequestParam("num") int num){
//        return prService.showTopRank(num);
//    }

    @PostMapping("/topK")
    public @ResponseBody List<String> pprtop(@RequestParam("k") int k, @RequestParam("algorithm") String algorithm){
        if (algorithm.equals("ppr"))
            return pprService.showTopRank(k);
        else
            return prService.showTopRank(k);
    }
}
