package com.example.demo7;

import  java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Report {
    public void generate(List<Expense> expenses){
        double total =0;
        Map<String,Double> categoryTotals = new HashMap<>();
        for(Expense e:expenses){
            total += e.getAmount();
            String category= e.getCategory().getcategory();
            categoryTotals.put(
                    category ,
                    categoryTotals.getOrDefault(category,0.0)+e.getAmount()

            );
        }

        System.out.println("\n======REPORT======");
        System.out.println("Total spending: "+total);
        System.out.println("number of transiction: " +expenses.size());
        System.out.println("\nspending by categories: ");
        for(String cat:categoryTotals.keySet()){
            System.out.println(cat+": "+categoryTotals.get(cat));
        }
        System.out.println("==================");

    }
}
