# Tiny-HTTP-server
Supported GET method for HTTP/1.0 and HTTP/1.1

--------------------------------------------------------------------------------------------------------------------
High-level description of the assignment and what your program(s) does:
The program is designed to handle HTTP/1.0 and HTTP/1.1 GET requests.
Its a multithreaded program which waits for TCP connection and spawns the thread to handle each connection request.
Every thread is responsible for serving the GET request. POST and other methods are not supported.
The design supports least support the 200, 404, 403, and 400 status codes. 
Fro HTTP/1.0 the connection is closed right away unless a header with connection: keep-alive is provided.
For HTTP/1.1 the connection remains open by default for 10 seconds.
---------------------------------------------------------------------------------------------------------------------------------
A list of submitted files:

1.Server.java
2.HTML,CSS, images downloaded from the SCU index webpage.
3.Script file
4.Make file
---------------------------------------------------------------------------------------------------------------------------------
Instructions for running your program:

to Compile :
javac Server.java

to run (edit the path as per the file lcation):
java Server -document_root "/Users/Vaishali/Submission" -port 8888 

to test 
1. Go to URL:hocalhost:8888 via  browser to retrieve home page
2. To test HTTP/1.1 via telnet you can use the following steps
   a. telnet localhost 8888
   b. Once connected please provide atleast 3 HTTP headers as follows:
      GET / HTTP/1.1 (press Enter)
      Host: localhost (press Enter)
      Accept: text (press Enter)
3.To test HTTP/1.0 via telnet you can use the following steps
      GET / HTTP/1.0 (press Enter)
      Host: localhost (press Enter)
      Accept: text (press Enter)
4. To test HTTP/1.0 with keep-alive header via telnet you can use the following steps
      GET / HTTP/1.0 (press Enter)
      Host: localhost (press Enter)
      Connection: keep-lives (press Enter)
