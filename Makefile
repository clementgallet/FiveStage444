CC=g++
CFLAGS=
LDFLAGS=
EXEC=FiveStage444
SRC= $(wildcard *.cpp)
OBJ= $(SRC:.cpp=.o)

all: $(EXEC)

FiveStage444: $(OBJ)
	$(CC) -o $@ $^ $(LDFLAGS)

%.o: %.cpp
	$(CC) -o $@ -c $< $(CFLAGS)

.PHONY: clean mrproper

clean:
	rm *.o

mrproper: clean
	rm $(EXEC)
