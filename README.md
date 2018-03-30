# Tiny-HTTP-server
Supported GET method for HTTP/1.0 and HTTP/1.1

(make sure the current directory is where the file is residing)
To Compile code :
javac Server.java
To run (edit the path as per the file location):
java Server -document_root "/Users/Vaishali/Submission" -port 8888
To test
1. Go to URL:hocalhost:8888 via browser to retrieve home page
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
