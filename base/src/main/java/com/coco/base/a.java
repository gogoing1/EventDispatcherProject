package com.coco.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 2018/8/11.
 */

public class a {

    public static void main(String[] args){
        List al=new ArrayList();
        al.add(0, 8);
        al.add(1,10);
        al.add(2, 3);
        al.add(3,20);
        al.add(4,30);
        al.add(2,70); //在第三个元素的位置加入一个元素
        for(int i=0;i<al.size();i++){
            System.out.print(al.get(i)+" ");
        }
    }
}
