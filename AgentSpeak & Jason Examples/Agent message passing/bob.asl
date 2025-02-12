+hello[source(A)] <-
	.print("I received 'hello' from ",A);
	.print("Sending back 'hello' to ",A);
	.send(A,tell,hello).