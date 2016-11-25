import java.io.*;

import java.util.*;
import java.util.Map.Entry;

class article
{
    int id ;
    double mag;
    int cluster;
    double simwithmax;
   
    LinkedHashMap<Integer, Integer> freqv = new LinkedHashMap<Integer, Integer>();
   
    LinkedHashMap<Integer, Integer> ngram = new LinkedHashMap<Integer, Integer>();
}

class cluster
{
    int id;
    List<article> members = new ArrayList<article>();
    double mag;
    LinkedHashMap<Integer, Double> centroid = new LinkedHashMap<Integer,Double>();
    LinkedHashMap<Integer,Integer> diversity = new LinkedHashMap<Integer,Integer>();
}

public class sphkmeans {
   
    public static boolean isNumeric(String str)
    {
      return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }


public static double cosine(article a, article b)
{
    double ans = 0;
    article fix = new article();
    article other = new article();
    if (a.freqv.size() < b.freqv.size())
    {
        fix = a;
        other = b;
    }
   
    else
    {
        fix = b;
        other = a;
    }
     Iterator<Entry<Integer, Integer>> ite = fix.freqv.entrySet().iterator();
     
     while (ite.hasNext())
    {
         Entry<Integer, Integer> e =  (Entry<Integer, Integer>)ite.next();
         int key = e.getKey();
         int val = e.getValue();
         
             if(other.freqv.containsKey(key))
             {
                 double val2 = other.freqv.get(key);
                 ans = ans+ (val*val2);
             }
             
         
         
    }
    ans = ans/(computeMag(a)*computeMag(b));
    if(ans > 1.0)
    {
    	ans = 1.0;
    }
    return ans;
}

public static double cosine_sim(article a, cluster b)
{
    double ans = 0;
    Iterator<Entry<Integer, Integer>> ite = a.freqv.entrySet().iterator();
     
     while (ite.hasNext())
    {
         Entry<Integer, Integer> e =  (Entry<Integer, Integer>)ite.next();
         int key = e.getKey();
         double val = e.getValue();
         
             if(b.centroid.containsKey(key))
             {
                 double val2 = b.centroid.get(key);
                 ans = ans+ (val*val2);
             }
             
         
         
    }
     
    ans = ans/(a.mag*b.mag);
    
    if(ans>1.0)
    {
    	ans = 1.0;
    }
    
    return ans;
}


public static double computeMag(article a)
{
    double ans = 0;
    Collection<Integer> x = a.freqv.values();
    for (Integer i:x)
    {
        ans = ans+i*i;
    }
    return Math.sqrt(ans);
   
}

public static double computeM(cluster c)
{
    double ans = 0;
    Collection<Double> x = c.centroid.values();
    for (double d:x)
    {
        ans = ans+ d*d;
    }
    return Math.sqrt(ans);
}


public static int reassign_clusters(List<article> alist, List<cluster> clist)
{
    int pointmove=0;
   
    for (article a: alist)
    {    
    	int mincentroid = a.cluster;
        int currindex = a.cluster;
        for (int i = 0; i<clist.size();i++)
        {
            if (cosine_sim(a, clist.get(i)) > a.simwithmax)
            {    pointmove = pointmove+1;
                a.simwithmax = cosine_sim(a, clist.get(i));
                mincentroid = i;
            }
        }
       
        clist.get(mincentroid).members.add(a);
        a.cluster = mincentroid;
        clist.get(currindex).members.remove(a);
   
    }
   
	return pointmove;

  
   

}

public static void print_clusters(List<cluster> cluster_list)
{

   
    for (cluster c: cluster_list)
    {
        System.out.print("Cluster "+c.id);
       
        for (article a: c.members)
        {
            System.out.println(" Member: "+a.id);
           
        }
       
        System.out.print(" "+c.mag);
        System.out.println(c.centroid.toString());
    }
       
       

}

public static double log2(double n)
{
    return (Math.log(n) / Math.log(2));
}

public static LinkedHashMap<Integer, Double> recompute_centroid(cluster c)
{
    LinkedHashMap<Integer, Double> temp = new LinkedHashMap<Integer,Double>();
   
    for (article a: c.members)
    {
         Iterator<Entry<Integer, Integer>> ite = a.freqv.entrySet().iterator();
         
         while (ite.hasNext())
        {
             Entry<Integer, Integer> e =  (Entry<Integer, Integer>)ite.next();
             int key = e.getKey();
             double val = e.getValue();
             
                 if(temp.containsKey(key))
                 {
                     double curr = temp.get(key);
                     val = val + curr;
                     temp.put(key,val);
                 }
                 
                 else
                 {
                    temp.put(key,val);
                 }
                 
             
             
        }
    }

     Iterator<Entry<Integer, Double>> ite = temp.entrySet().iterator();
     double size = c.members.size();
     while (ite.hasNext())
    {
         Entry<Integer, Double> e =  (Entry<Integer, Double>)ite.next();
         int key = e.getKey();
         double val = e.getValue();
         val = val/size;
         temp.put(key,val);
   
    }
     
     return temp;
}


public static double compute_sse(List<article>alist, List<cluster> clist)
{    double sse = 0;
    for (article a:alist)
    {
        sse = sse+ cosine_sim(a, clist.get(a.cluster));
    }
    return sse;
}

public static double  entropy(List<cluster> clist)
{
	double [] entlist = new double[clist.size()];
	
	for (int i = 0;i<clist.size(); i++)
	{
		entlist[i] = 0;
	}
	
	LinkedHashMap<Integer,Integer> div = new LinkedHashMap<Integer,Integer>();
	Collection<Integer> values = new ArrayList<Integer>();
	double size = 0;
	double netsize = 0;
	for (cluster c:clist)
	{   netsize = netsize+c.members.size();
		div = c.diversity;
		values = c.diversity.values();
		double ent = 0;
		for (double x:values)
		{
			size = size+x;
		}
		
		for (double x:values)
		{
			double prob;
			prob= x/size;
			ent = ent - (prob * log2(prob));
		}
	
	entlist[c.id] = ent;	
	}
	double netentropy = 0;
	for (int i = 0; i<entlist.length; i++)
	{
		double weight = clist.get(i).members.size()/netsize;
		netentropy = netentropy + (entlist[i]*weight);
	}
	return netentropy;
	
}
public static double  purity(List<cluster> clist, List<article> alist)
{
	double [] purlist = new double[clist.size()];
	double netsize = 0;
	for (int i = 0;i<clist.size(); i++)
	{
		purlist[i] = 0;
	}
	
	LinkedHashMap<Integer,Integer> div = new LinkedHashMap<Integer,Integer>();
	Collection<Integer> values = new ArrayList<Integer>();
	double size = 0;
	double netpur = 0;
	for (cluster c:clist)
	{   
		
		div = c.diversity;
		values = c.diversity.values();
		double max = 0;
		for (double x : values)
		{if(x > max)
		{
			max = x;
		}
		
		}
		netpur = netpur + max;
	}
	return netpur/alist.size();
	
}




    public static void main(String[] args) {
    	
    String file = args[0];
	String fil = args[1];
    	int noofclusters = Integer.parseInt(args[2]);
	int nooftrials = Integer.parseInt(args[3]);
	String outputfile = args[4];
	
	
       final long starttime = System.currentTimeMillis();
       
       
        List<article> article_list = new ArrayList<article>();
        int flag = 0;
        double sse= 0;
        double overallent = 0;
        double overallpur = 0;
        double overallsse = 0;
        
        
        LinkedHashMap<Integer,Integer> found_articles = new LinkedHashMap<Integer,Integer>();
        LinkedHashMap<Integer, Integer> classdist = new LinkedHashMap<Integer,Integer>();
        
        
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            String[]a = new String[3];
           
            while ((line = br.readLine()) != null  ) {
                a = line.split(",");
                int id = Integer.parseInt(a[0]);
                int dim = Integer.parseInt(a[1]);
                int freq = Integer.parseInt(a[2]);
               
                if (found_articles.containsKey(id))
                {
                    for(article x: article_list)
                    {
                        if (x.id == id)
                        {
                            x.freqv.put(dim, freq);
                        }
                    }
                }
               
                else
                {
                    found_articles.put(id, 1);
                    article temp = new article();
                    temp.id = id;
                    temp.freqv.put(dim, freq);
                    article_list.add(temp);
                   
                }
                flag = flag + 1;
            }
        }
        
       
       
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        
        try (BufferedReader br = new BufferedReader(new FileReader(fil))) {
            String line;
            String[]a = new String[2];
           
            while ((line = br.readLine()) != null  ) {
                a = line.split(",");
                int id = Integer.parseInt(a[0]);
                int klass = Integer.parseInt(a[1]);
               
                classdist.put(id,klass);
        }
        }
        
        catch (FileNotFoundException e1) {
        	
        	System.out.print("I am here");
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			
			System.out.print("I am here");// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        int [] seeds = {1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31,33,35,37,39};
        
        for(int t = 0;t<nooftrials;t++)
    {    System.out.println("\n -------TRIAL "+t+"-------");  
        int len = article_list.size();
       
        List<Integer> cent = new ArrayList<Integer>();
        Random randomno = new Random(seeds[t]);
        
         for(int i = 0;i<noofclusters;i++)
           {
             cent.add(randomno.nextInt(len));
           }
       
      
     
      
      

      
        List<cluster >cluster_list = new ArrayList<cluster>() ;
       
        int p = 0;
     
        //INITIAL CENTROID ASSIGNMENT
        List<article> other_articles = new ArrayList<article>();
       
       
        for (article x: article_list)
        {
            x.mag = computeMag(x);
        }
        for (int i = 0; i<article_list.size();i++)
        {  
       
            for (int j = 0; j < cent.size();j++)
            {
       
            if (i == cent.get(j))
            {   article_list.get(i).simwithmax = 1;
                article_list.get(i).cluster = p;
                cluster temp = new cluster();
                temp.id = p;
                
                p++;
                Iterator<Entry<Integer, Integer>> ite = article_list.get(i).freqv.entrySet().iterator();
               
                 while (ite.hasNext())
                    {
                         Entry<Integer, Integer> e =  (Entry<Integer, Integer>)ite.next();
                         int key = e.getKey();
                         double val = e.getValue();
                         temp.centroid.put(key, val);
                         
                    }
                 
                 temp.members.add(article_list.get(i));
                 temp.mag = article_list.get(i).mag;
                 cluster_list.add(temp);
            }
           
            }
           
           
            other_articles.add(article_list.get(i));
            }
        
        
       
        for (int i = 0; i<other_articles.size();i++)
        {
            for(int j = 0; j<cluster_list.size();j++)
            {   for (article a: cluster_list.get(j).members)
            {
                if(other_articles.get(i) == a)
                {
                    other_articles.remove(other_articles.get(i));
                }
            }
            }
        }
       
       
        
       
        
        
     
        //INITIALISE THINGS FOR ARTICLES OTHER THAN INITIAL CENTROIDS
        for (article a: other_articles)
        {    double maxcos = 0;
            int index = 0;
           
            for (int i = 0; i<cluster_list.size();i++)
            {
                double sim = cosine_sim(a,cluster_list.get(i));
                if (sim > maxcos)
                {
                    maxcos = sim;
                    index = i;
                   
                }
               
                   
            }
           
        a.cluster = index;
        a.simwithmax = maxcos;
        cluster_list.get(index).members.add(a);
        }
       
        
        for (cluster c: cluster_list)
        {   c.centroid = recompute_centroid(c);
           
            c.mag = computeM(c);
            
        }
       
        
       

   
   
        sse = compute_sse(article_list, cluster_list);
        //System.out.println("\nDone Initial distribution with an objective net-similarity function value of: "+sse);
       
       
        //CLUSTERING BEGINS
       
    //print_clusters(cluster_list);
   int moves = 1;
       
        while (moves!=0)
           
        {
        	
        	
            moves = reassign_clusters(article_list, cluster_list);

            for (cluster c: cluster_list)
            {	
            	c.centroid = recompute_centroid(c);
                c.mag = computeM(c);
                
            }    
       
        sse = compute_sse(article_list, cluster_list);
       
       // System.out.println("\n\nThe new value of objective net-similarity is: "+sse);
       // System.out.println("There are still "+cluster_list.size()+" clusters");
       
      
        
       
       
    }
        System.out.print("Trial "+t+" ended with a net similarity of: "+sse);
        for (cluster c: cluster_list)
        {
        	for (article a: c.members)
        	{	
        		int id = a.id;
        		int klass = classdist.get(id);
        		
        		if(c.diversity.containsKey(klass))
        		{
        			int curr = c.diversity.get(klass);
        			curr = curr+1;
        			c.diversity.put(klass,curr);
        		}
        		else
        		{
        			c.diversity.put(klass,1);
        		}
        		
        		
        	}
        }
        
   
    
        
        double netentropy = entropy(cluster_list);
     
        double netpurity = purity(cluster_list, article_list);
        
        
       
        System.out.print("\n");
        
        
        if(netpurity > overallpur)
        { 
         overallpur = netpurity;
         overallent = netentropy;
         overallsse = compute_sse(article_list, cluster_list); 
        try( PrintWriter out = new PrintWriter(outputfile)){
            for (article a: article_list)
            {
            out.print(a.id+" c"+a.cluster+" "+ " "+a.simwithmax);
            out.print("\n");
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }   
        
    }
        
        final long endTime = System.currentTimeMillis();
        System.out.println("\n The best entropy is: "+overallent);
        System.out.println("\nThe best purity is: "+overallpur);
        System.out.println("\n The best Objective function value is: "+overallsse);
        System.out.print("Congrats Miner, you have completed your execution in "+(endTime - starttime)/1000+" seconds");
    
    }
    
  
      }
