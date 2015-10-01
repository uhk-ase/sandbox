# Sandbox #

Just some place for experiments and benchmarks.

### Requirements ###

- Java 7 or newer
- Maven

### Compile ###

    mvn package

### Running ###

	java -jar target/sandbox.jar COMMAND_NAME

## Network benchmark ##

Point-to-point messages transportation - "naive" Java sockets vs. ZeroMQ. Speed is in messages per second.

### Running ###

	java -jar target/sandbox.jar network-benchmark

### Results Intel Core i5-3230M, 16GB DDR3, **loop-back** ###

**Simple test** 

- 1 server
- 1 client
- 1 000 000 messages sent one-by-one

**Multi-threaded test**

- 1 server
- 10 parallel clients
- 1 000 000 messages sent one-by-one by each client = 10 000 000 messages in total

#### Run #1 (debug) ####

Test name | Loss | Speed
----------|------|----------------------------------
**ZeroMQ - simple** 			| 	0% 		| 48368
**ZeroMQ - multi-threaded** 	|	0,12% 	| 62106
**Naive Java - simple** 		|	86% 	| 331 
**Naive Java - multi-threaded**	|	82%		| 1060

#### Run #2 (release + clean PC) ####

Test name | Loss | Speed
----------|------|----------------------------------
**ZeroMQ - simple** 			| 	0%		| 105932
**ZeroMQ - multi-threaded** 	|	0,07%	| 132289
**Naive Java - simple** 		|	86% 	| 674 
**Naive Java - multi-threaded**	|	5%		| 2308

#### Run #3 (release) ####

Rerun because of java weird result, but still persist...

Test name | Loss | Speed
----------|------|----------------------------------
**ZeroMQ - simple** 			| 	0%		| 96126
**ZeroMQ - multi-threaded** 	|	0,1%	| 107136
**Naive Java - simple** 		|	84% 	| 776 
**Naive Java - multi-threaded**	|	18%		| 2170
