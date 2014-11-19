GCC = g++
CPPFLAGS = -g -finline-functions -Wall -Winline -pipe -ffast-math
TARGET = WordSplit
OBJ = main.o Dict.o Hash.o Lat.o FindLetterAndNumber.o

all: $(TARGET)
$(TARGET) : $(OBJ)	
	$(GCC) -g -o $@ $^

%.o	: %.cpp	
	$(GCC) -O2 $(CPPFLAGS) -c $< -o $@

clean:	
	rm -rf *.o
