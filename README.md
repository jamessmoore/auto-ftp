Auto FTP
========

The idea behind Auto FTP is to listen on servers for files that move around often. If you have a remote datastore and need to receive files often, you'll need to manually connect via FTP and download them. Auto FTP will listen on a connection for new files -- files you're interested in -- and will automatically add them to a download queue and process them.

Project Status
--------------

**FTP Support**
In Progress

**SFTP Support**
Done

**FTPS Support**
Not Yet Implemented

**User Configuration** 
Download directory can be set.
Filter lists have not yet been implemented.
Host config has not yet been implemented.


Build Status
------------

[![Build Status](https://travis-ci.org/JAGFin1/auto-ftp.png?branch=master)](https://travis-ci.org/JAGFin1/auto-ftp)
