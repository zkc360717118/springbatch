package com.kevin.springbatch.springbatch.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NonSense {
    public static void main(String[] args) {
        String[] x = {"a", "b"};
        ArrayList list = new ArrayList();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        System.out.println(list.toString());

        String d = "d";
        System.out.println("NonSense.main");
        System.out.println("args = " + Arrays.deepToString(args));
    }
}
