from bs4 import BeautifulSoup

import glob, os, operator, re, time

start_time = time.clock()


def ngrams(s,n):
    
    grammap = {}

    for i in range(0,len(s)-n):
        gram = s[i:i+n]
        b = list(gram)
        for x in range(0,len(b)):
            if b[x] == " ":
                b[x] = "_"

        gram = ''.join(b)
        if grammap.has_key(gram):
            grammap[gram] = grammap[gram]+1
        else:
            grammap[gram] = 1

    return grammap
    
class article:

    new_id = " "
    Body = " "
    topic = " "
    bag = []
    wordmap = {}
    map3 = {}
    map5 = {}
    map7 = {}
    dwordmap = {}
    d3map = {}
    d5map = {}
    d7map = {}
    klass = 0
folder = "reuters21578"   
desired_articles = []
final_articles = []


os.chdir("reuters21578")
id_list = []
total_len = 0
l = {}

global_wordmap = {}
dw = 0
global_3grammap = {}
d3 = 0
global_5grammap = {}
d5 = 0
global_7grammap = {}
d7 = 0

for x in glob.glob("*.sgm"):
    f = open(x, 'r')
    soup = BeautifulSoup(f, 'html.parser')

    reuter_list = list(soup.find_all("reuters"))

    for x in reuter_list:       
        a = list(x.topics)

   
        if len(a) == 1:
            curr = article()
            curr.new_id = x["newid"]
            curr.Body = x.body
            curr.topic = x.topics
            desired_articles.append(curr)
            d = list(x.topics)
           
            for topic in d:
                if l.has_key(topic.text):
                    l[topic.text] = l[topic.text]+1
                else:
                    l[topic.text] = 1

sorted_l = sorted(l.items(), key=operator.itemgetter(1),reverse = True)
sorted_l = sorted_l[0:20]




       
freq_map= dict(sorted_l)       
           
       




for x in desired_articles:
   
    if freq_map.has_key(x.topic.text):
        final_articles.append(x)

print "Length of final Articles : "+str(len(final_articles))




os.chdir("..")

#DATA CLEANING
idlist = []


f = open("output.txt","w")


for x in final_articles:
    freqv = {}
    ngram = {}
    ngram5 = {}
    ngram7 = {}
    
    if x.Body is not None:
        
        f.write(x.new_id+"\n")
        f.write(x.topic.text+"* \n")
    
        a = x.Body.text
        #a = a.decode('utf-8').strip()
    
        a= a.encode('ascii', errors='ignore')
        a= a.lower()
        a= re.sub('[^0-9a-z]+', ' ',a)
        collect = []
        collect = a.split()
        bag=[]
        
        for guy in collect:
            
            if guy.isdigit() == False:
                bag.append(guy)

                if freqv.has_key(guy):
                    freqv[guy] = freqv[guy]+1
                    
                else:
                    freqv[guy] = 1
        
        s = ' '.join(bag)
        x.wordmap = freqv

       
        ngram = ngrams(s,3)
        ngram5 = ngrams(s,5)
        ngram7 = ngrams(s,7)

        x.map3 = ngram
        x.map5 = ngram5
        x.map7 = ngram7
        
        for guy, hisfrequency in freqv.iteritems():
            f.write(guy+":"+str(hisfrequency)+"|")
            if global_wordmap.has_key(guy):
                donothing = 0
            else:
                dw = dw+1
                global_wordmap[guy] = dw
                
                    
            
        f.write("\n")

        for guy, hisfrequency in ngram.iteritems():
            f.write(guy+":"+str(hisfrequency)+"|")
            if global_3grammap.has_key(guy):
                donothing = 0
            else:
                d3 = d3+1
                global_3grammap[guy] = d3
                
        for guy, hisfrequency in ngram5.iteritems():
            
            if global_5grammap.has_key(guy):
                donothing = 0
            else:
                d5 = d5+1
                global_5grammap[guy] = d5
                
        for guy, hisfrequency in ngram7.iteritems():
            
            if global_7grammap.has_key(guy):
                donothing = 0
            else:
                d7 = d7+1
                global_7grammap[guy] = d7

                            
        f.write("\n")
   
f.close()








count_map = {}
count_3map = {}
count_5map = {}
count_7map = {}

for x in final_articles:
    
    #freqv = x.wordmap
    for guy,hisfrequency in x.wordmap.iteritems():
        if count_map.has_key(guy):
            count_map[guy] = count_map[guy]+ 1
        else:
            count_map[guy] = 1
    
    for guy, hisfrequency in x.map3.iteritems():
        if count_3map.has_key(guy):
            count_3map[guy] = count_3map[guy]+1
        else:
            count_3map[guy] = 1
    for guy, hisfrequency in x.map5.iteritems():
        if count_5map.has_key(guy):
            count_5map[guy] = count_5map[guy]+1
        else:
            count_5map[guy] = 1
    for guy, hisfrequency in x.map7.iteritems():
        if count_7map.has_key(guy):
            count_7map[guy] = count_7map[guy]+1
        else:
            count_7map[guy] = 1
new_map = {}
new_3map = {}
new_5map = {}
new_7map = {}

for guy,hisfrequency in count_map.iteritems():
    if hisfrequency > 4:
        new_map[guy] = hisfrequency

for guy,hisfrequency in count_3map.iteritems():
    if hisfrequency > 4:
        new_3map[guy] = hisfrequency

for guy,hisfrequency in count_5map.iteritems():
    if hisfrequency > 4:
        new_5map[guy] = hisfrequency

for guy,hisfrequency in count_7map.iteritems():
    if hisfrequency > 4:
        new_7map[guy] = hisfrequency

print "The length of the frequent words is: "+str(len(new_map))
print "The length of the frequent 3 guys is: "+str(len(new_3map))
print "The length of the frequent 5 guys is: "+str(len(new_5map))
print "The length of the frequent 7 guys is: "+str(len(new_7map))

dw = 0

final_wlabels = {}
for guy, hisfrequency in new_map.iteritems():
    if final_wlabels.has_key(guy):
        donothing = 0
    else:
        final_wlabels[guy] = dw
        dw = dw+1
        
final_3labels = {}
d3 = 0
for guy, hisfrequency in new_3map.iteritems():
    if final_3labels.has_key(guy):
        donothing = 0
    else:
        final_3labels[guy] = d3
        d3 = d3+1
        
final_5labels = {}
d5 = 0
for guy, hisfrequency in new_5map.iteritems():
    if final_5labels.has_key(guy):
        donothing = 0
    else:
        final_5labels[guy] = d5
        d5 = d5+1

final_7labels = {}
d7 = 0
for guy, hisfrequency in new_7map.iteritems():
    if final_7labels.has_key(guy):
        donothing = 0
    else:
        final_7labels[guy] = d7
        d7 = d7+1

#WRITING THE CLASS LABELS WITH THEIR NOS.
    
f = open("bag.clabel","w")
for guy, hislabel in final_wlabels.iteritems():
    f.write(guy+":"+str(hislabel)+"\n")
f.close()

f = f = open("char3.clabel","w")
for guy, hislabel in final_3labels.iteritems():
    f.write(guy+":"+str(hislabel)+"\n")
f.close()

f = f = open("char5.clabel","w")
for guy, hislabel in final_5labels.iteritems():
    f.write(guy+":"+str(hislabel)+"\n")
f.close()

f = f = open("char7.clabel","w")
for guy, hislabel in final_7labels.iteritems():
    f.write(guy+":"+str(hislabel)+"\n")
f.close()

for x in final_articles:
    
    
    freqv = x.wordmap
    grammap3 = x.map3
    grammap5 = x.map5
    grammap7 = x.map7
    
    newfreqv = {}
    
    for guy, hisfrequency in freqv.iteritems():

        if final_wlabels.has_key(guy):
            
            a = final_wlabels[guy]
            b = freqv[guy]

            newfreqv[a] = b
    x.dwordmap = newfreqv
    
    new3 = {}
    for guy, hisfrequency in grammap3.iteritems():
        if final_3labels.has_key(guy):
            
            a = final_3labels[guy]
            b = grammap3[guy]

            new3[a] = b
            
    x.d3map = new3
    
    new5 = {}
    for guy, hisfrequency in grammap5.iteritems():
        if final_5labels.has_key(guy):
            
            a = final_5labels[guy]
            b = grammap5[guy]

            new5[a] = b
            
    x.d5map = new5
    
    new7 = {}
    for guy, hisfrequency in grammap7.iteritems():
        if final_7labels.has_key(guy):
            a = final_7labels[guy]
            b = grammap7[guy]

            new7[a] = b
    x.d7map = new7

#WRITING THE CLASS LABELS IS DONE, SO NOW TO WRITING EACH ARTICLE AS A CSV VECTOR
f = open("bag.csv","w")

countw = 0
for x in final_articles:
    
    for guy, hisfrequency in x.dwordmap.iteritems():
        
        countw = countw+1
        f.write(str(x.new_id)+","+str(guy)+","+str(hisfrequency)+"\n")
f.close()

count3 = 0                    
f = open("char3.csv","w")
for x in final_articles:
    
    for guy, hisfrequency in x.d3map.iteritems():
        count3 = count3+1
        f.write(str(x.new_id)+","+str(guy)+","+str(hisfrequency)+"\n")
f.close()

count5 = 0
f = open("char5.csv","w")
for x in final_articles:
    
    for guy, hisfrequency in x.d5map.iteritems():
        count5 = count5+1
        f.write(str(x.new_id)+","+str(guy)+","+str(hisfrequency)+"\n")
f.close()
                    

count7 = 0
f = open("char7.csv","w")
for x in final_articles:
    
    for guy, hisfrequency in x.d7map.iteritems():
        count7 = count7+1
        f.write(str(x.new_id)+","+str(guy)+","+str(hisfrequency)+"\n")
f.close()

tc = 0
topic_labels = {}

for x in final_articles:
    if topic_labels.has_key(x.topic.text):
        donothing = 0
    else:
        topic_labels[x.topic.text] = tc
        tc = tc+1

for x in final_articles:
    x.klass = topic_labels[x.topic.text]

    
f = open("topiclabels.txt","w")

    
for guy, hisfrequency in topic_labels.iteritems():
    f.write(str(guy)+":"+str(hisfrequency)+"\n")
    
f.close()

f = open("reuters21578.class","w")
for x in final_articles:
    
    
    f.write(str(x.new_id)+","+str(x.klass)+"\n")
f.close()

print "I wrote "+str(countw)+" non negative values for bag o' words"
print "I wrote "+str(count3)+" non negative values for 3 guys"
print "I wrote "+str(count5)+" non negative values for 5 guys"
print "I wrote "+str(count7)+" non negative values for 7 guys"
tim = str(time.clock()-start_time)
print "Congrats Miner! You have cleaned the data in "+tim+" seconds"


