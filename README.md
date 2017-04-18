# Aloha-Client

For more information, about the aloha project, check out 
[Aloha](https://github.com/zalora/aloha "Aloha") and 
[Aloha-Server](https://github.com/zalora/aloha-server "Aloha-Server").

## Configuration

tl;dr: `java -Xmx256m -Xms256m -jar aloha-client-*.jar`

### Memory Settings

The client is pretty lean: After running the gc, memory consumption is ~20MB. `-Xmx256m -Xms256m` is a generous setting.

### Server and Cluster Settings

#### Cluster Mode

If you only have one server node, you have to set `infinispan.cluster.mode` to `server`. In production you should have
more than one node, then you have to set this value to `cluster`.

#### Initial Server

You need to give the client a seed IP for the startup. Afterwards it will be kept up to date on topology changes and
will also take care of fail-overs. If you are on AWS, it's fairly easy to get an IP address from the AWS API. Have a
look at the very powerful `aws` cli!

Here's an example how you could find out your ECS instance based on the cluster name: 
```
aws ec2 describe-instances --filters Name=tag:Name,Values=*aloha-server-sg \
    Name=instance-state-name,Values=running | jq .Reservations[0].Instances[0].PrivateIpAddress
```

You can define the initial server name with this key: `infinispan.cluster.initialServer: 10.221.123.5`

#### Cluster Name 

The client needs the cluster name for the initial connect. Infinispan supports cluster-failover, so every cluster needs
a unique name. 

The name can be set with this key: `infinispan.cluster.name: aloha-server-sg`

#### Caching

Caching is disabled by default. If you activate it, you have to make sure that you provide enough heap memory to 
accommodate all items in the cache. The cache is running in [invalidation-mode](http://infinispan.org/docs/stable/user_guide/user_guide.html#invalidation_mode)
and you have to provide the maximum number of entries the cache can hold. If this number is exceeded, the cache will
evict items with the LRU algorithm.

That's how you configure the cache:

```
infinispan:
  
  ...
  
  cache:
    enabled: false
    maxEntries: 50000
```

So the keys would be: 

- `infinispan.cache.enabled`
- `infinispan.cache.maxEntries`

#### Example

`infinispan.cluster.mode: cluster`
`infinispan.cluster.initialServer: 10.221.123.5`
`infinispan.cluster.name: aloha-sg`
`infinispan.cache.enabled: false`

## Load Balancing

If you are running a replication cluster, the infinispan client will balance the load on all available nodes via 
Round-Robin. In distributed mode, the client will hash the keys to find the owner of the server and only open 
connections to this specific server to reduce the load.