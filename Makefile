JFLAGS = -g
JC = javac
JAVA = java
DIR = cg/fivestage444
TABLES = $(DIR)/fivephase_tables_ftm

default:
	mkdir -p $(DIR)
	cp *.java $(DIR)
	$(JC) $(JFLAGS) $(DIR)/*.java

run:
	$(JAVA) $(DIR)/Main

tables:
	$(JAVA) $(DIR)/Main $(TABLES)

clean:
	$(RM) $(DIR)/*.class
	$(RM) $(DIR)/*.java
