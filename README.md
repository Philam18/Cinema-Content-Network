# Cinema Content Network

![alt text](https://i.imgur.com/8yU8uq3.png "Mapping of key Network Components")
_Image by Philip lam_

---

### Experimenting with networks through implementation of the key network components in an example _media-content provider service_.  
This project focusses on the involvement at the _Application_, _Transport_, and _Network_ layers of the [OSI-Model](https://en.wikipedia.org/wiki/OSI_model).  
**All implementation is done through Java**.

The intention of this project is to model the underlying process between a client and a server, and consequently the process of DNS resolution through intermediate name-servers running on those respective domains.
This exact process is describable in the following way:
- A client visits the provider's web-server - indicating to the web-server its initial visitation through a _request for the root-level directory_ `/` in its HTTP header
- The provider, who responds with `index.html`, has listed inside available media-resources the client can request from it in the form of URLs (for simplicity's sake)
- The client will then attempt to resolve this URL through its Local DNS
- The local DNS will then begin the resolution process, eventually reaching the provider's CDN endpoint
- After the resolution returns, client can then request the specific resource from the CDN, who will then transfer the media to the client.

It consists of 6 main components:

#### Client

The Client component represents a typical user that visits the domain to request resources from the provider. In this case the content is video (though the media type is both irrelevant and mutable).

#### HisCinema Web Server

The `www.HisCinema.com` web-server is the primary interface in client interaction. It is here that the client connects and makes requests for resources (Note: the client is oblivious to the fact that it will ultimately receive these resource from a CDN).
As so, HisCinema must appropriately handle `200 OK` and `404 Not Found` HTTP response codes upon receiving requests for non-existent media files, though this shouldn't ever be the case - `www.HisCinema.com` web-server will only allow the client to
 request files that it can provide. It provides set of URL's for the user to select (of the form `video.hisCinema.com/<Video Name>.mp4`), wherein the client can then resolve through its own Local DNS.

#### HisCinema Name Server

`ns.HisCinema.com`, like any name-server, attempts to resolve DNS queries for its specific domains. HisCinema's name server contains a [resource record](https://en.wikipedia.org/wiki/List_of_DNS_record_types) that directs incoming requests to it's
 Content Deliverer's domain `ns.HerCDN.com`.

#### HerCDN Name Server

`ns.HerCDN.com` is the final step in the DNS resolution. It will direct incoming requests to its webserver `www.herCDN.com`, (i.e return the IP address of HerCDN web-server). Note that the DNS resolution process is considered successful/over when a
 DNS server returns an IP address.

#### HerCDN Web Server

`www.herCDN.com` is responsible for the transfer of media files to the client.

#### Local DNS

The client's Local DNS attempts to resolve requests for video URLs. Typically, after resolution, Local DNS's cache results to improve look-up speeds for subsequent queries - this function is not implemented.


## Requirements

This application was built and compiled using the JDK 10 (Compiler `javac 1.8.0_161`).  
Get the latest Java SDK here: http://www.oracle.com/technetwork/java/javase/downloads/index.html

## Compiling and Running

Go into each respective component folder and simply compile and run.  

##### Client
```
javac Client.java
java Client
```

##### Local DNS
```
javac LocalDNS.java
java LocalDNS
```

##### HisCinema
```
javac HisCinema.java
java HisCinema
```

##### HisCinema NS
```
javac HisCinemaNS.java
java HisCinemaNS
```
##### HerCDN
```
javac HerCDN.java
java HerCDN
```
##### HerCDN NS
```
javac HerCDNNS.java
java HerCDNNS
```

## Configuration
The most simplest single-machine configuration to get started only requires you to press `Enter` a few times. The application will already be set up to use the default ports 40280 - 40284 (obviously provided there isn't already
 some application using that port already), and IP addresses set to `localhost` (127.0.0.1).

To test on multiple machines, simply input the IP and Ports of those machines you wish to connect to. One example configuration could be as follows:

Server | IP:Port
:---: | :---:
HisCinema | 192.168.0.1:9000   
HisCinema NS | 192.168.0.1:9001  
HerCDN | 192.168.0.2:9000
HerCDN NS | 192.168.0.2:9001  
Local DNS | 192.168.0.3:9000
