package com.jwcq.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortList<E> {
    public void Sort(List<E> list, final String method, final String sort) {
        Collections.sort(list, new Comparator() {
            public int compare(Object a, Object b) {

                int ret = 0;


                try {
                    Method m1 = ((E) a).getClass().getMethod(method, null);
                    Method m2 = ((E) b).getClass().getMethod(method, null);
                    Object first=m1.invoke(((E) a), null);
                    Object second=m2.invoke(((E) b), null);
                    if(first instanceof Double){
                        Double d_a=(Double)first;
                        Double d_b=(Double)second;
                        if (sort != null && "desc".equals(sort))ret =d_b.compareTo(d_a);
                        else ret= d_a.compareTo(d_b);
                    }
                    else if(first instanceof Integer){
                        Integer d_a=(Integer)first;
                        Integer d_b=(Integer)second;
                        if (sort != null && "desc".equals(sort))ret =d_b.compareTo(d_a);
                        else ret= d_a.compareTo(d_b);
                    }
                    else if(first instanceof Float){
                        Float d_a=(Float)first;
                        Float d_b=(Float)second;
                        if (sort != null && "desc".equals(sort))ret =d_b.compareTo(d_a);
                        else ret= d_a.compareTo(d_b);
                    }else{
                        if (sort != null && "desc".equals(sort))// 倒序
                            ret = second.toString()
                                    .compareTo(first.toString());
                        else
                            // 正序
                            ret = first.toString()
                                    .compareTo(second.toString());
                    }

                } catch (NoSuchMethodException ne) {
                    System.out.println(ne);
                } catch (IllegalAccessException ie) {
                    System.out.println(ie);
                } catch (InvocationTargetException it) {
                    System.out.println(it);
                }

                return ret;
            }
        });
    }
}