package com.jqyzyh.learn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * @author yuhang
 */

public class OneObject {
    public static void main(String[] args){

        Scanner scan = new Scanner(System.in);
        int length = scan.nextInt();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Integer str = scan.nextInt();
            list.add(str);
        }

        int[] ns = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ns[i] = list.get(i).intValue();
        }


        OneObject o = new OneObject(length, ns);

        System.out.println(o.createResult());
    }

    static class Number{
        int number;

        int[] numbers;

        public Number(int number) {
            this.number = number;
            if (number < 10){
                numbers = new int[]{number};
            }else if(number < 100){
                numbers = new int[2];
                numbers[0] = number / 10;
                numbers[1] = number % 10;
            }else {
                numbers = new int[3];
                numbers[0] = number / 100;
                numbers[1] = (number / 10) % 10;
                numbers[2] = number % 10;
            }
        }

        public int getWeigth(){
            int weigth = (3 - numbers.length) << 28;
            if (numbers.length > 1) {
                weigth += numbers[1] << 16;
            }

            if (numbers.length > 2){
                weigth += numbers[2];
            }

            return weigth;
        }
    }

    int n;
    ArrayList<Number>[] groups = new ArrayList[9];

    public OneObject(int n, int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            throw new RuntimeException("numbers is empty!!!");
        }
        if (numbers.length != n) {
            throw new RuntimeException("n != numbers length!!!");
        }
        this.n = n;
        for (int i = 0; i < this.groups.length; i++) {
            this.groups[i] = new ArrayList<>();
        }

        for (int i = 0; i < numbers.length; i++) {
            Number number = new Number(numbers[i]);
            this.groups[number.numbers[0] - 1].add(number);
        }

        for (ArrayList<Number> group : groups) {
            Collections.sort(group, new Comparator<Number>() {
                @Override
                public int compare(Number o1, Number o2) {
                    return o2.getWeigth() - o1.getWeigth();
                }
            });
        }
    }

    String createResult(){
        StringBuilder sb = new StringBuilder();

        for (int i = 8; i >= 0; i --){
            List<Number> group = groups[i];
            for (Number n : group) {
                sb.append(n.number);
            }
        }
        return sb.toString();
    }
}
