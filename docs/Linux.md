# Linux

-------------------------

[TOC]

### Command Line Tools

 * ##### Process threads, cpu and memory usage.

   ```bash
   $ ps  -p  <pid> \
         -T \
         -o start_time,uname,fname,pid,ppid,%cpu,%mem,tid,tname,thcount,time,size,rss,m_size,vsize,lwp | awk '{print $6}' | sort -rn | less

   # Show process tree
   $ pstree -aps

   # Show all threads
   $ ls /proc/<pid>/task

   # Show all threads using top
   $ top -H -p <pid>
   ```



 * ##### Sysctl

   ```bash
   sysctl -w fs.file-max=12000500
   sysctl -w fs.nr_open=20000500
   ulimit -n 20000000
   sysctl -w net.ipv4.tcp_mem='10000000 10000000 10000000'
   sysctl -w net.ipv4.tcp_rmem='1024 4096 16384'
   sysctl -w net.ipv4.tcp_wmem='1024 4096 16384'
   sysctl -w net.core.rmem_max=16384
   sysctl -w net.core.wmem_max=16384
   sysctl -w net.core.somaxconn=1024
   ```

   - See [2M websocket connections](http://www.phoenixframework.org/blog/the-road-to-2-million-websocket-connections)

   - See [Tuning HA Proxy for 300K connections](https://www.linangran.com/?p=547)

   - See [Kernel Tuning params](https://tweaked.io/guide/kernel/)

   - See [How TCP backlog works in Linux](http://veithen.github.io/2014/01/01/how-tcp-backlog-works-in-linux.html)

   - See [TCP TIME_WAIT config](http://www.fromdual.com/huge-amount-of-time-wait-connections)



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
     ```



   * Download a file with retry

     ```bash
     # -fsSLO
     $curl --fail \
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

  ★ See [Wireshark Over SSH](https://kaischroed.wordpress.com/2013/01/28/howto-use-wireshark-over-ssh/)

  ★ See [Using Unix Named Pipe](https://serverfault.com/a/530020/184962)
