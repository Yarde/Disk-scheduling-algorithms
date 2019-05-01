//Disk scheduling algorithms - simulation of algorithms FCFS, SSTF, SCAN, C-SCAN, EDF, FD-SCAN.
//Compare their execution time (estimated by the total movement of disk head).

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Old{
    public static int requestsInQueueAmount;
    public static int orderSCAN = 0;

    static class Request{
        int deadline, platterNo, trackNo, sectorNo;
        public Request(int deadline, int platterNo, int trackNo, int sectorNo) {
            this.deadline = deadline;
            this.platterNo = platterNo;
            this.trackNo = trackNo;
            this.sectorNo = sectorNo;
        }
        public String toString(){
            return "dl: "+deadline+" platter: "+platterNo+" track: "+trackNo+" sector: "+sectorNo;
        }
    }

    public static List<Request> randomProcesses(int n){
        List<Request> list = new ArrayList<>();
        for(int i=0; i<n; i++)
            list.add(new Request(ThreadLocalRandom.current().nextInt(1,100),
                    ThreadLocalRandom.current().nextInt(0,7),
                    ThreadLocalRandom.current().nextInt(0,1023),
                    ThreadLocalRandom.current().nextInt(0,63)));
        return list;
    }

    public static void FCFS(List<Request> a){
        List<Request> all = new ArrayList<>(a);
        List<Request> list = new ArrayList<>();;
        fill(list,all);
        int pSwitch=0, tSwitch=0;
        while(list.size()>1){
            if(!all.isEmpty())
                fill(list,all);
            if(list.get(0).platterNo != list.get(1).platterNo)pSwitch++;
            if(list.get(0).trackNo != list.get(1).trackNo)tSwitch++;
            list.remove(0);
        }
        System.out.println("With FCFS switched:\n-platter: "+pSwitch+"\n-track: "+tSwitch);
    }

    public static void SSTF(List<Request> a){
        List<Request> all = new ArrayList<>(a);
        List<Request> list = new ArrayList<>();;
        fill(list,all);
        int pSwitch=0, tSwitch=0;
        while(list.size()>1){
            if(!all.isEmpty())
                fill(list,all);
            int closest = findClosest(list);
            //System.out.print(closest+",");
            if(list.get(0).platterNo != list.get(closest).platterNo)pSwitch++;
            if(list.get(0).trackNo != list.get(closest).trackNo)tSwitch++;
            list.set(0,list.get(closest));
            list.remove(closest);
        }
        System.out.println("With SSTF switched:\n-platter: "+pSwitch+"\n-track: "+tSwitch);
    }

    public static void SCAN(List<Request> a){
        List<Request> all = new ArrayList<>(a);
        List<Request> list = new ArrayList<>();;
        fill(list,all);
        int pSwitch=0, tSwitch=0;
        while(list.size()>1){
            if(!all.isEmpty())
                fill(list,all);
            int closest = scanNext(list);
//            System.out.print(closest+" *** "+list.get(0).toString()+",\n");
            if(closest == -1) closest = scanNext(list);
            if(closest == -1) break;
            if(list.get(0).platterNo != list.get(closest).platterNo)pSwitch++;
            if(list.get(0).trackNo != list.get(closest).trackNo)tSwitch++;
            list.set(0,list.get(closest));
            list.remove(closest);
        }
        System.out.println("With SCAN switched:\n-platter: "+pSwitch+"\n-track: "+tSwitch);
    }

    public static void CSCAN(List<Request> a){
        List<Request> all = new ArrayList<>(a);
        List<Request> list = new ArrayList<>();;
        fill(list,all);
        int pSwitch=0, tSwitch=0;
        while(list.size()>1){
            if(!all.isEmpty())
                fill(list,all);
            int closest = findNext(list);
            if(list.get(0).platterNo != list.get(closest).platterNo)pSwitch++;
            if(list.get(0).trackNo != list.get(closest).trackNo)tSwitch++;
            list.set(0,list.get(closest));
            list.remove(closest);
        }
        System.out.println("With C-SCAN switched:\n-platter: "+pSwitch+"\n-track: "+tSwitch);
    }

    public static void SDF(List<Request> a){
        List<Request> all = new ArrayList<>(a);
        List<Request> list = new ArrayList<>();;
        fill(list,all);
        int pSwitch=0, tSwitch=0;
        while(list.size()>1){
            if(!all.isEmpty())
                fill(list,all);
            int closest = findSD(list);
            if(list.get(0).platterNo != list.get(closest).platterNo)pSwitch++;
            if(list.get(0).trackNo != list.get(closest).trackNo)tSwitch++;
            list.set(0,list.get(closest));
            list.remove(closest);
        }
        System.out.println("With SDF switched:\n-platter: "+pSwitch+"\n-track: "+tSwitch);
    }

    public static void FDSCAN(List<Request> a){
        List<Request> all = new ArrayList<>(a);
        List<Request> list = new ArrayList<>();;
        fill(list,all);
        int pSwitch=0, tSwitch=0;
        while(list.size()>1){
            if(!all.isEmpty())
                fill(list,all);
            int closest = scanSD(list);
//            System.out.print(closest+" *** "+list.get(0).toString()+",\n");
            if(closest == -1) closest = scanNext(list);
            if(closest == -1) break;
            if(list.get(0).platterNo != list.get(closest).platterNo)pSwitch++;
            if(list.get(0).trackNo != list.get(closest).trackNo)tSwitch++;
            list.set(0,list.get(closest));
            list.remove(closest);
        }
        System.out.println("With FD-SCAN switched:\n-platter: "+pSwitch+"\n-track: "+tSwitch);
    }

    public static int findClosest(List<Request> a){
        int index = 1;
        int pdiff = 9, tdiff = 1025;
        for(int i = 2; i < a.size(); i++) {
            if (Math.abs(a.get(i).platterNo - a.get(0).platterNo) <=  pdiff) {
                if(Math.abs(a.get(index).trackNo - a.get(0).trackNo) <= tdiff){
                    pdiff = Math.abs(a.get(i).platterNo - a.get(0).platterNo);
                    tdiff = Math.abs(a.get(index).trackNo - a.get(0).trackNo);
                    index = i;
                    continue;
                }
            }
        }
        return index;
    }

    public static int scanNext(List<Request> a){
        int index = 0;
        if(orderSCAN == 0){
            int pdiff = 9, tdiff = 1025;
            for(int i = 1; i < a.size(); i++) {
                if (a.get(i).platterNo >= a.get(0).platterNo &&  a.get(i).platterNo - a.get(0).platterNo <= pdiff) {
                    if(a.get(i).trackNo >= a.get(0).trackNo && a.get(i).trackNo - a.get(0).trackNo <= tdiff){
                        pdiff = a.get(i).platterNo - a.get(0).platterNo;
                        tdiff = a.get(i).trackNo - a.get(0).trackNo;
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
            int pdiff = -9, tdiff = -1025;
            for(int i = 1; i < a.size(); i++) {
                if (a.get(i).platterNo <= a.get(0).platterNo &&  a.get(i).platterNo - a.get(0).platterNo >= pdiff) {
                    if(a.get(i).trackNo <= a.get(0).trackNo && a.get(i).trackNo - a.get(0).trackNo >= tdiff){
                        pdiff = a.get(i).platterNo - a.get(0).platterNo;
                        tdiff = a.get(i).trackNo - a.get(0).trackNo;
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

    public static int findNext(List<Request> a){
        int index = 0;
        int pdiff = 9, tdiff = 1025;
        for(int i = 1; i < a.size(); i++) {
            if (a.get(i).platterNo >= a.get(0).platterNo &&  a.get(i).platterNo - a.get(0).platterNo <= pdiff) {
                if(a.get(i).trackNo >= a.get(0).trackNo && a.get(i).trackNo - a.get(0).trackNo <= tdiff){
                    pdiff = a.get(i).platterNo - a.get(0).platterNo;
                    tdiff = a.get(i).trackNo - a.get(0).trackNo;
                    index = i;
                    continue;
                }
            }
        }
        if (index == 0) {
            pdiff = 9;
            tdiff = 1025;
            int sdiff = 65;
            for(int i = 1; i < a.size(); i++) {
                if (a.get(i).platterNo <= pdiff) {
                    if(a.get(i).trackNo <= tdiff){
                        if(a.get(i).sectorNo <= sdiff) {
                            pdiff = a.get(i).platterNo;
                            tdiff = a.get(i).trackNo;
                            sdiff = a.get(i).sectorNo;
                            index = i;
                            continue;
                        }
                    }
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
            int pdiff = 9, tdiff = 1025;
            for(int i = 1; i < a.size(); i++) {
                if(a.get(i).deadline < sd){
                    if (a.get(i).platterNo >= a.get(0).platterNo &&  a.get(i).platterNo - a.get(0).platterNo <= pdiff) {
                        if(a.get(i).trackNo >= a.get(0).trackNo && a.get(i).trackNo - a.get(0).trackNo <= tdiff){
                            pdiff = a.get(i).platterNo - a.get(0).platterNo;
                            tdiff = a.get(i).trackNo - a.get(0).trackNo;
                            index = i;
                            sd = a.get(i).deadline;
                            continue;
                        }
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
            int pdiff = -9, tdiff = -1025;
            for(int i = 1; i < a.size(); i++) {
                if(a.get(i).deadline < sd) {
                    if (a.get(i).platterNo <= a.get(0).platterNo && a.get(i).platterNo - a.get(0).platterNo >= pdiff) {
                        if (a.get(i).trackNo <= a.get(0).trackNo && a.get(i).trackNo - a.get(0).trackNo >= tdiff) {
                            pdiff = a.get(i).platterNo - a.get(0).platterNo;
                            tdiff = a.get(i).trackNo - a.get(0).trackNo;
                            index = i;
                            sd = a.get(i).deadline;
                        }
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
        List<Request> list = randomProcesses(200);
//        for(Object x: list){
//            System.out.println(x.toString());
//        }
        requestsInQueueAmount = 50;
        FCFS(list);
        SSTF(list);
        SCAN(list);
        CSCAN(list);
        SDF(list);
        FDSCAN(list);
    }
}
