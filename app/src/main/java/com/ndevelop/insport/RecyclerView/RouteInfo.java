package com.ndevelop.insport.RecyclerView;

public class RouteInfo {
   // private int img;
    private int distance;
    private String time;

    public RouteInfo(int distance, String time){
    this.distance=distance;
   // this.img=img;
    this.time=time;

    }
    public int getDist(){
        return this.distance;
    }
    public String getTime(){
        return  this.time;
    }
    //public int getImg(){
    //    return  this.img;
   // }
}
