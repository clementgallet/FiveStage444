JFLAGS = -g -Xlint:unchecked
JC = javac
JAVA = java
DIR = cg/fivestage444
TABLES = $(DIR)/fivephase_tables

default:
	mkdir -p $(DIR)
	cp *.java $(DIR)
	cp -r Coordinates $(DIR)
	cp -r Stages $(DIR)
	$(JC) $(JFLAGS) $(DIR)/*/*.java
	$(JC) $(JFLAGS) $(DIR)/*.java
	# ctags -f ~/.tags -R ./ $(JAVA_HOME)src

run:
	$(JAVA) $(DIR)/Main

test:
	$(JAVA) $(DIR)/Test

analyze:
	$(JAVA) $(DIR)/Analyze

profile:
	$(JAVA) -javaagent:../jip/profile.jar $(DIR)/Main

tables:
	$(JAVA) $(DIR)/Tools $(TABLES)

clean:
	$(RM) $(DIR)/*.class
	$(RM) $(DIR)/*.java
	$(RM) $(DIR)/*/*.java
	$(RM) $(DIR)/*/*.class
