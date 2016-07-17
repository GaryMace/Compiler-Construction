@Echo On 

call jjtree grammar.jjt
call javacc grammar.jj
call javac *.java
call java Parser < in_file.txt
call del *.class
@pause