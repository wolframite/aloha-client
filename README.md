# Aloha-Client

For more information, about the aloha project, check out 
[Aloha](https://github.com/zalora/aloha "Aloha") and 
[Aloha-Server](https://github.com/zalora/aloha-server "Aloha-Server").

## Configuration

tl;dr: `java -Xmx256m -Xms256m -jar aloha-client-*.jar`

### Memory Settings

The client is pretty lean: After running the gc, memory consumption is ~20MB. `-Xmx256m -Xms256m` is a generous setting.

### Server and Cluster Settings

There are two parameters, which have to be modified for production: 

- `infinispan.cluster.initialServer: localhost`
- `infinispan.cluster.name: Kamehameha`

One initial server is enough as the client is a smart client, which keeps up to date about topology changes. For our
replicated setup it will also loadbalance calls between all available servers.