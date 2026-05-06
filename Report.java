package com.example.demo7;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Report {
    public void generate(List<Transaction> transactions){
        double total =0;
        double get=0;
        Map<String,Double> categoryTotals = new HashMap<>();
        for(Transaction e:transactions){
            if(e.getCategory()!=null){
                total += e.getAmount();
                String category= e.getCategory().getcategory();
                categoryTotals.put(
                        category ,
                        categoryTotals.getOrDefault(category,0.0)+e.getAmount()
                );
            }
            else{
                get+=e.getAmount();
            }

        }

        System.out.println("\n======REPORT======");
        System.out.println("Total spending: "+total);
        System.out.println("Total Incomes: "+get);
        System.out.println("number of Spend transiction: " +categoryTotals.size());
        System.out.println("number of Income transiction: " +(transactions.size()-categoryTotals.size()));
        System.out.println("number of transiction: " +transactions.size());
        System.out.println("\nspending by categories: ");
        for(String cat:categoryTotals.keySet()){
            System.out.println(cat+": "+categoryTotals.get(cat));
        }
        System.out.println("==================");

    }
}
