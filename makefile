kmeans:
	javac sphkmeans.java

preprocess:
	python dataclean.py

clean:
	rm *.class *.csv *.clabel *.txt
