//Disk scheduling algorithms - simulation of algorithms FCFS, SSTF, SCAN, C-SCAN, EDF, FD-SCAN.
//Compare their execution time (estimated by the total movement of disk head).

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Main{
    public static int requestsInQueueAmount;
    public static int orderSCAN = 0;

    static class Request{
        int deadline, trackNo;
        public Request(int deadline, int trackNo) {
            this.deadline = deadline;
            this.trackNo = trackNo;
        }
        public String toString(){
            return "dl: "+deadline+" track: "+trackNo;
        }
    }

    public static List<Request> randomProcesses(int n){
        List<Request> list = new ArrayList<>();
        for(int i=0; i<n; i++)
            list.add(new Request(ThreadLocalRandom.current().nextInt(1,100), ThreadLocalRandom.current().nextInt(0,1023)));
        return list;
    }

    public static void FCFS(List<Request> a){
        List<Request> all = new ArrayList<>(a);
        List<Request> list = new ArrayList<>();;
        fill(list,all);
        int tSwitch = 0;
        while(list.size()>1){
            if(!all.isEmpty())
                fill(list,all);
            if(list.get(0).trackNo != list.get(1).trackNo)
                tSwitch += Math.abs(list.get(1).trackNo - list.get(0).trackNo);
            System.out.print(list.get(0).trackNo+",");
            list.remove(0);
        }
        System.out.println("\nWith FCFS switched:\n"+tSwitch+" track\n");
    }

    public static void SSTF(List<Request> a){
        List<Request> all = new ArrayList<>(a);
        List<Request> list = new ArrayList<>();;
        fill(list,all);
        int tSwitch=0;
        while(list.size()>1){
            if(!all.isEmpty())
                fill(list,all);
            int closest = findClosest(list);
            if(list.get(0).trackNo != list.get(closest).trackNo)
                tSwitch += Math.abs(list.get(closest).trackNo - list.get(0).trackNo);
            System.out.print(list.get(0).trackNo+",");
            list.set(0,list.get(closest));
            list.remove(closest);
        }
        System.out.println("\nWith SSTF switched:\n"+tSwitch+" track\n");
    }

    public static void SCAN(List<Request> a){
        List<Request> all = new ArrayList<>(a);
        List<Request> list = new ArrayList<>();;
        fill(list,all);
        int tSwitch=0;
        while(list.size()>1){
            if(!all.isEmpty())
                fill(list,all);
            int closest = scanNext(list);
            System.out.print(list.get(0).trackNo+",");
            if(closest == -1) closest = scanNext(list);
            if(closest == -1) break;
            if(list.get(0).trackNo != list.get(closest).trackNo)
                tSwitch += Math.abs(list.get(closest).trackNo - list.get(0).trackNo);
            list.set(0,list.get(closest));
            list.remove(closest);
        }
        System.out.println("\nWith SCAN switched:\n"+tSwitch+" track\n");
    }

    public static void CSCAN(List<Request> a){
        List<Request> all = new ArrayList<>(a);
        List<Request> list = new ArrayList<>();;
        fill(list,all);
        int tSwitch=0;
        while(list.size()>1){
            if(!all.isEmpty())
                fill(list,all);
            int closest = findNext(list);
            if(list.get(0).trackNo != list.get(closest).trackNo)
                tSwitch += Math.abs(list.get(closest).trackNo - list.get(0).trackNo);
            System.out.print(list.get(0).trackNo+",");
            list.set(0,list.get(closest));
            list.remove(closest);
        }
        System.out.println("\nWith C-SCAN switched:\n"+tSwitch+" track\n");
    }

    public static void SDF(List<Request> a){
        List<Request> all = new ArrayList<>(a);
        List<Request> list = new ArrayList<>();;
        fill(list,all);
        int tSwitch=0;
        while(list.size()>1){
            if(!all.isEmpty())
                fill(list,all);
            int closest = findSD(list);
            if(list.get(0).trackNo != list.get(closest).trackNo)
                tSwitch += Math.abs(list.get(closest).trackNo - list.get(0).trackNo);
            System.out.print(list.get(0).trackNo+",");
            list.set(0,list.get(closest));
            list.remove(closest);
        }
        System.out.println("\nWith SDF switched:\n"+tSwitch+" track\n");
    }

    public static void FDSCAN(List<Request> a){
        List<Request> all = new ArrayList<>(a);
        List<Request> list = new ArrayList<>();;
        fill(list,all);
        int tSwitch=0;
        while(list.size()>1){
            if(!all.isEmpty())
                fill(list,all);
            int closest = scanSD(list);
            if(closest == -1) closest = scanNext(list);
            if(closest == -1) break;
            if(list.get(0).trackNo != list.get(closest).trackNo)
                tSwitch += Math.abs(list.get(closest).trackNo - list.get(0).trackNo);
            System.out.print(list.get(0).trackNo+",");
            list.set(0,list.get(closest));
            list.remove(closest);
        }
        System.out.println("\nWith FD-SCAN switched:\n"+tSwitch+" track\n");
    }

    public static int findClosest(List<Request> a){
        int index = 0;
        int tdiff = 1025;
        for(int i = 1; i < a.size(); i++) {
            int diff = Math.abs(a.get(i).trackNo - a.get(0).trackNo);
            if(diff <= tdiff){
                tdiff = diff;
                index = i;
                continue;
            }
        }
        return index;
    }

    public static int scanNext(List<Request> a){
        int index = 0;
        if(orderSCAN == 0){
            int tdiff = 1025;
            for(int i = 1; i < a.size(); i++) {
                int diff = a.get(i).trackNo - a.get(0).trackNo;
                if(a.get(i).trackNo >= a.get(0).trackNo && diff <= tdiff){
                    tdiff = diff;
                    index = i;
                    continue;
                }
            }
            if (index == 0){
                orderSCAN = 1;
                return -1;
            }else{
                return index;
            }
        }
        if(orderSCAN == 1){
            int tdiff = -1025;
            for(int i = 1; i < a.size(); i++) {
                int diff = a.get(i).trackNo - a.get(0).trackNo;
                if(a.get(i).trackNo <= a.get(0).trackNo && diff >= tdiff){
                    tdiff = diff;
                    index = i;
                    continue;
                }
            }
            if (index == 0){
                orderSCAN = 0;
                return -1;
            }else{
                return index;
            }
        }
        return -1;
    }

    public static int findNext(List<Request> a){
        int index = 0;
        int tdiff = 1025;
        for(int i = 1; i < a.size(); i++) {
            int diff = a.get(i).trackNo - a.get(0).trackNo;
            if(a.get(i).trackNo >= a.get(0).trackNo && diff <= tdiff){
                tdiff = diff;
                index = i;
                continue;
            }
        }
        if (index == 0) {
            tdiff = 1025;
            for(int i = 1; i < a.size(); i++) {
                if(a.get(i).trackNo <= tdiff){
                    tdiff = a.get(i).trackNo;
                    index = i;
                    continue;
                }
            }
        }
        return index;
    }

    public static int findSD(List<Request> a){
        int index = 0;
        int sd = 101;
        for(int i = 1; i < a.size(); i++) {
            if(a.get(i).deadline < sd){
                sd = a.get(i).deadline;
                index = i;
            }
        }
        return index;
    }

    public static int scanSD(List<Request> a){
        int index = 0;
        int sd = 101;
        if(orderSCAN == 0){
            int tdiff = 1025;
            for(int i = 1; i < a.size(); i++) {
                if(a.get(i).deadline < sd){
                    int diff = a.get(i).trackNo - a.get(0).trackNo;
                    if(a.get(i).trackNo >= a.get(0).trackNo && diff <= tdiff){
                        tdiff = diff;
                        index = i;
                        continue;
                    }
                }
            }
            if (index == 0){
                orderSCAN = 1;
                return -1;
            }else{
                return index;
            }
        }
        if(orderSCAN == 1){
            int tdiff = -1025;
            for(int i = 1; i < a.size(); i++) {
                if(a.get(i).deadline < sd) {
                    int diff = a.get(index).trackNo - a.get(0).trackNo;
                    if(a.get(i).trackNo <= a.get(0).trackNo && diff >= tdiff){
                        tdiff = diff;
                        index = i;
                        continue;
                    }
                }
            }
            if (index == 0){
                orderSCAN = 0;
                return -1;
            }else{
                return index;
            }
        }
        return -1;
    }

    public static List<Request> fill(List<Request> where, List<Request> from){
        while(where.size() != requestsInQueueAmount && !from.isEmpty()){
            where.add(from.get(0));
            from.remove(0);
        }
        return where;
    }

    public static void main(String[] args){
        List<Request> list = randomProcesses(1000);
//        for(Object x: list){
//            System.out.println(x.toString());
//        }
        requestsInQueueAmount = 300;
        FCFS(list);
        SSTF(list);
        SCAN(list);
        CSCAN(list);
        SDF(list);
        FDSCAN(list);
    }
}
