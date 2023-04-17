# Linux

<!-- TOC -->

* [Linux](#linux)
    * [Command Line Tools](#command-line-tools)
    * [Proc FileSystem](#proc-filesystem)
    * [Curl](#curl)
    * [SSH](#ssh)
    * [Wireshark](#wireshark)
    * [TCPDump](#tcpdump)
    <!-- TOC -->

### Command Line Tools

* ##### Process threads, cpu and memory usage.

  ```bash
  # Show process threads and it's CPU usage
  $ ps  -p  <pid> \
        -T \
        -o start_time,uname,fname,pid,ppid,%cpu,%mem,tid,tname,thcount,time,size,rss,m_size,vsize,lwp | awk '{print $6}' | sort -rn | less

  # OR
  $ ps  -p  <pid> \
        -T \
        -o start_time,uname,fname,pid,ppid,%cpu,%mem,tid,tname,thcount,time,size,rss,m_size,vsize,lwp --sort -tid

  # Show all processes sorted by RSS
  $ ps -eo user,pid,pmem,rss,vsz,comm,start,time,command --sort -rss | numfmt --header --from-unit=1024 --to=iec --field 4-5 | awk '$3 != 0'

  # Show process tree
  $ pstree -aps

  # Show all threads
  $ ls /proc/<pid>/task

  # Show all threads using top
  $ top -H -p <pid>
  ```


* ##### Sysctl

  ```bash
  # /etc/sysctl.conf
  fs.file-max=10485760
  fs.nr_open=10485760

  net.core.somaxconn=16192
  net.core.netdev_max_backlog=16192
  net.ipv4.tcp_max_syn_backlog=16192

  net.ipv4.ip_local_port_range = 1024 65535

  net.core.rmem_max = 16777216
  net.core.wmem_max = 16777216
  net.core.rmem_default = 16777216
  net.core.wmem_default = 16777216
  net.ipv4.tcp_rmem = 4096 87380 16777216
  net.ipv4.tcp_wmem = 4096 87380 16777216
  net.ipv4.tcp_mem = 1638400 1638400 1638400

  net.netfilter.nf_conntrack_buckets = 1966050
  net.netfilter.nf_conntrack_max = 7864200

  # EC2 Amazon Linux
  net.core.netdev_max_backlog = 65536
  net.core.optmem_max = 25165824
  net.ipv4.tcp_max_tw_buckets = 1440000

  # /etc/security/limits.conf (set max open file descriptors)
  * soft nofile 8000000
  * hard nofile 9000000
  ```

    - [**Java Virtual Threads - c5m**](https://github.com/ebarlas/project-loom-c5m#experiments)

    -
  See [2M websocket connections](http://www.phoenixframework.org/blog/the-road-to-2-million-websocket-connections)

    - See [Tuning HA Proxy for 300K connections](https://www.linangran.com/?p=547)

    - See [Kernel Tuning params](https://tweaked.io/guide/kernel/)

    -
  See [How TCP backlog works in Linux](http://veithen.github.io/2014/01/01/how-tcp-backlog-works-in-linux.html)

    - See [TCP TIME_WAIT config](http://www.fromdual.com/huge-amount-of-time-wait-connections)

   ```bash
    # Can also set it using sysctl commands
    sysctl -w fs.file-max=12000500
    sysctl -w fs.nr_open=20000500
    sysctl -w net.ipv4.tcp_mem='10000000 10000000 10000000'
    sysctl -w net.ipv4.tcp_rmem='1024 4096 16384'
    sysctl -w net.ipv4.tcp_wmem='1024 4096 16384'
    sysctl -w net.core.rmem_max=16384
    sysctl -w net.core.wmem_max=16384
    sysctl -w net.core.somaxconn=1024
    ulimit -n 20000000
   ```


* Netstat

  ```bash
  # Find all TCP/UDP listening port's PID
  $ netstat -tunalp

  # On Mac
  $ netstat -vanp tcp  | grep -i 8080

  # Find process used by given port
  $ lsof -i :8080

  # Socket stats
  $ ss -s
  ```


* XArgs

  ```bash
  sudo -S pgrep -f tomcat  | xargs -n 1  -I {}  sh -c 'echo "Killing process pid: {}" &&  sudo kill -9 {} && echo Done.'
  ```

### [Proc FileSystem](https://dashdash.io/5/proc)

* Checking the Init system

  ```bash
  case "$(cat /proc/1/comm)" in
      init)    echo Init ;;
      systemd) echo SystemD ;;
      upstart) echo Upstart ;;
      *) echo "unknown: '`cat /proc/1/comm`'" ;;
  esac

  # OR
  $ ps --no-headers -o comm 1 | grep -q 'systemd'
  ```

### Curl

* Get round trip time

  ```bash
  $ curl -X GET \
         -o /dev/null  \
         -I -w "Connect: %{time_connect}s, Transfer: %{time_starttransfer}s, Total: %{time_total}s" \
         -X GET http://www.google.com

  # Curl Response time using specific DNS server
  $  while true ;do curl \
        --dns-servers 8.8.8.8 \
        https://suresh.dev \
        -o /dev/null \
        -s \
        -w 'Total: %{time_total}s\n' \
  ;done
  ```


* Download latest release from Github

  ```bash
  # Download the latest release from Github
  $ LOCATION=$(curl -s https://api.github.com/repos/jvm-profiling-tools/async-profiler/releases/latest \
      | grep -i "browser_download_url" \
      | grep -i "converter.jar" \
      | awk '{ print $2 }' \
      | sed 's/,$//'       \
      | sed 's/"//g' )     \
      ; curl --progress-bar -L -o ${HOME}/install/tools/converter.jar ${LOCATION}
  ```


* Download a file with retry

  ```bash
  # -fsSLO
  $ curl --fail \
        --silent \
        --show-error \
        --location \
        --remote-name \
        --compressed \
        --progress-bar \
        --retry 3 \
        --retry-connrefused \
        --retry-delay 1 \
        --connect-timeout 5 \
        --max-time 10 \
        --request GET \
  https://search.maven.org/remotecontent?filepath=org/jetbrains/kotlin/kotlin-stdlib/1.7.0/kotlin-stdlib-1.7.0.jar
  ```

### DIG

* Commands

  ```bash
  # Find all nameserver IPs for a TLD
  $ for i in $(dig ns suresh.dev +short); do echo -n "$i " && dig $i +short; done

  # Trace DNS requests
  $ dig +trace compute.suresh.dev

  # Trace using specific resolver
  $ dig @a.root-servers.net +trace compute.suresh.dev
  ```

### SSH

- Port Forwarding

  ```bash
  # Local Port(2345) Forwarding
  ssh -v user@ip -L 2345:ip:5432
  # ssh -v ooadmin@100.64.0.32 -L 2345:100.64.0.32:5432

  # Remote Port Forwarding
  ssh -v  -R  :8091:localip:remoteport  user@remoteip
  # ssh -v  -R  :8091:172.28.170.95:3000  app@10.242.182.199
  ```

### Wireshark

- Download [wirkshark](https://www.wireshark.org/download.html)

- Install `tcpdump` on remote host (say `10.9.210.101`)

  ```bash
  $ ssh user@10.9.210.101 "sudo yum install -y tcpdump"
  ```

- Start packet capture

  ```bash
  $ ssh user@10.9.210.101 "sudo /usr/sbin/tcpdump -s0 -w - 'port 22'" | wireshark -k -i -
  # or for 8080
  $ ssh user@remote-host "sudo /usr/sbin/tcpdump -s0 -w - 'port 8080'" | wireshark -k -i -
  ```

    * See [Wireshark Over SSH](https://kaischroed.wordpress.com/2013/01/28/howto-use-wireshark-over-ssh/)
    * See [Using Unix Named Pipe](https://serverfault.com/a/530020/184962)

### [TCPDump](https://www.tcpdump.org/)

 ```bash
 # HTTP traffic including req & res headers and message body from a particular source.
 $ tcpdump -A -s 0 'src example.com and tcp port 80 and (((ip[2:2] - ((ip[0]&0xf)<<2)) - ((tcp[12]&0xf0)>>2)) != 0)'

 # All incoming HTTP GET traffic
 $ tcpdump -i any -s 0 -A 'tcp[((tcp[12:1] & 0xf0) >> 2):4] = 0x47455420'

 # All incoming HTTP POST traffic to port 80
 $ tcpdump -i any -s 0 -A 'tcp dst port 80 and tcp[((tcp[12:1] & 0xf0) >> 2):4] = 0x504F5354'

 # HTTP GET/POST  Incoming calls to port 80/443 Originating from 192.168.10.1 Host.
 $ tcpdump -i any -s 0 -A 'tcp dst port 80 or tcp dst port 443 and tcp[((tcp[12:1] & 0xf0) >> 2):4] = 0x47455420 or tcp[((tcp[12:1] & 0xf0) >> 2):4] = 0x504F5354' and host 192.168.10.1

 # Filter HTTP User agents
 $ tcpdump -vvAls0 | grep 'User-Agent:'

 # Write to pcap file
 $ tcpdump -i any -s 0 -X -w /tmp/tcpdump.pcap

 # Capture TCP packets from local interface
 $ tcpdump -i lo

 # Print packet in ASCII
 $ tcpdump -v -i lo -A
 ```

* https://danielmiessler.com/study/tcpdump/
