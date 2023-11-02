/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.c
 * Author: arsi
 *
 * Created on November 1, 2023, 8:18 PM
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <ctype.h>
#include <errno.h>
#include <unistd.h>
#include <stdarg.h>
#include <syslog.h>

#include <arpa/inet.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <linux/netlink.h>
#include <linux/rtnetlink.h>
#include <net/if.h>
#include <ifaddrs.h>
#include <stdio.h>
#include <sys/socket.h>
#include <unistd.h>

#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <sys/poll.h>

#include <time.h>
#include <pthread.h>
#include <uuid/uuid.h>
#include <signal.h>

void daemonize(void)
{
	FILE *fp;
	int maxfd;
	int i;

	/* fork #1: exit parent process and continue in the background */
	if ((i = fork()) < 0)
	{
		perror("couldn't fork");
		exit(2);
	} else if (i > 0)
	{
		exit(0);
	}

	/* fork #2: detach from terminal and fork again so we can never regain
	* access to the terminal */
	setsid();
	if ((i = fork()) < 0)
	{
		perror("couldn't fork #2");
		exit(2);
	} else if (i > 0)
	{
		exit(0);
	}

	/* write pid */
	if ((fp=fopen("/var/run/wsdd.pid", "w")) != NULL)
	{
		fprintf(fp, "%d", getpid());
		fclose(fp);
	}

	/* change to root directory and close file descriptors */
	if (chdir("/"))
	maxfd = getdtablesize();
	for (i = 0; i < maxfd; i++)
		close(i);

	/* use /dev/null for stdin, stdout and stderr */
	open("/dev/null", O_RDONLY);
	open("/dev/null", O_WRONLY);
	open("/dev/null", O_WRONLY);
}

/*
 * 
 */
int main(int argc, char** argv) {
    daemonize();
    sleep(35);
    system("/customer/customer.sh");
    return (EXIT_SUCCESS);
}

