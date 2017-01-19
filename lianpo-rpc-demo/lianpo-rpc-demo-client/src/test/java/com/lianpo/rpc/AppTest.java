package com.lianpo.rpc;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Unit test for simple App.
 */
public class AppTest{

    public static void main(String[] args){
        List<String> names = new ArrayList<String>();
        names.add("maichao");
        names.add("jack");
        names.add("john");


        Collections.sort(names, (o1, o2) -> o1.compareTo(o2));

        List<String> upperNames = names.stream().map((String name) -> {
            return name.toUpperCase();
        }).collect(Collectors.toList());

        List<String> lowerNames = names.stream().map(name -> name.toLowerCase()).collect(Collectors.toList());
        System.out.println(names);

        System.out.println(upperNames);
    }

}