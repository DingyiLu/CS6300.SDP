javac -cp lib\encode.jar;lib\junit-4.12.jar;lib\hamcrest-core-1.3.jar -d testclasses testsrc\edu\gatech\seclass\encode\*
cd testclasses
jar -cf tests.jar edu\gatech\seclass\encode\MyMainTest.class
cd ..
copy testclasses\tests.jar lib\tests.jar
del testclasses\tests.jar
java -cp lib\tests.jar;lib\encode.jar;lib\junit-4.12.jar;lib\hamcrest-core-1.3.jar; org.junit.runner.JUnitCore edu.gatech.seclass.encode.MyMainTest > report.txt
