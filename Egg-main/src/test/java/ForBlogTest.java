/*
 * Copyright (c) 2015 Andrew-Wang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.*;

/**
 * Created by andrew on 15-3-31.
 */

public class ForBlogTest{
    public static void main(String[] args) {
        Set<String> set = new HashSet<String>();
        set.add("1");
        set.add(null);
        System.out.println(set.add("1"));
    }
}

//class A{
//
//    {
//        System.out.println("----->A的初始化块");
//    }
//
//    static {
//        System.out.println("----->A的静态初始化块");
//    }
//
//    public A(){
//        System.out.println("----->A的构造器");
//    }
//}
//
//class B extends A{
//    {
//        System.out.println("---->B的初始化块");
//    }
//
//    static {
//        System.out.println("---->B的静态初始化块");
//    }
//
//    public B(){
//        System.out.println("---->B的构造器");
//    }
//}
//
//class C extends B{
//    {
//        System.out.println("--->C的初始化块");
//    }
//
//    static {
//        System.out.println("--->C的静态初始化块");
//    }
//
//    public C(){
//        System.out.println("--->C的构造器");
//    }
//}
//
//class D{
//    {
//        num = 4;
//    }
//
//    private int num = 3;
//
//    public D(){
//    }
//
//    public int getNum(){
//        return num;
//    }
//
//}
//
//class E{
//    public void echo() {
//        System.out.println("-------->这是父类中的echo");
//    }
//}
//
//class F extends E{
//    public void echo(){
//        System.out.println("----->这是子类中的echo");
//    }
//    public void test(){
//
//    }
//}
//
//public class ForBlogTest{
//    public static void main(String[] args) {
//        E f = new F();
//        f.echo();
//    }
//}

//public class ForBlogTest {
//    public static void main(String[] args) {
////        C c = new C();
////        System.out.println("----------------------------------分割线----------------------------------");
////        C c1 = new C();
//        D d = new D();
//        System.out.println(d.getNum());
//    }
//}
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("1", "123");
//        map.put("2", "234");
//        map.put("3", "345");
//        Set<String> set = map.keySet();
//        System.out.println(map);
//        set.remove("2");
//        System.out.println(map);

//        Map<A, String> map1 = new TreeMap<A, String>();
//        A a = new A(1);
//        A b = new A(2);
//        map1.put(b, "123");
//        map1.put(a, "234");
//
//    }
//
//    static class A {
//           private int a;
//        public A(int a){
//            this.a = a;
//        }
//    }

