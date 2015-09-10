test:
	sbt 'run --backend c --compile --test --genHarness --targetDir build --vcd'

verilog:
	sbt 'run --backend v --targetDir build'

clean:
	rm -f *.cpp *.h *.o

.PHONY: test verilog clean
