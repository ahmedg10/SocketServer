# SocketServer
Attempting to Create Quote of The Day Socket Server

## New Learned Knowledge:

    ### What does ExectorService Unlock for us, when creating this server? 
    - In the code, we intalized the thread-pool to handle 5 threads at time. So, we can have 5 connections happen at the same time, and if we exceed that then, we will put those connections on queue uuntil the threadpool is has avalaiblility

    ### InputStream and OutputStream:
    - In is the reading the data from the socket
    - Out is writing the data to the socket 
    -
