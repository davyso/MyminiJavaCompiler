JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	miniJava/Compiler.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) miniJava/*.class
	$(RM) miniJava/AbstractSyntaxTrees/*.class
	$(RM) miniJava/ContextualAnalyzer/*.class
	$(RM) miniJava/SyntacticAnalyzer/*.class
