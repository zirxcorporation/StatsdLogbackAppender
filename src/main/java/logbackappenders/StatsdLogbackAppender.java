package logbackappenders;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

/**
 * Created by richard on 5/17/16.
 *
 * This class is called in logback.xml and sends stats to statsd/graphite
 * Note the logback.xml configuration for conf files that might need to be moved locally vs in production (application.conf vs server.conf)
 *
 * Summary spreadsheet for graphite stats - https://docs.google.com/spreadsheets/d/1E8klTkxB1hQYvgarKNTU7w0JtX8UASC1G5mrAtN89rk/edit#gid=0
 */
public class StatsdLogbackAppender extends AppenderBase<ILoggingEvent> {
  private static StatsDClient statsd;
  private static String statsdPrefix; // prefix that will become the filepath (delimited by '.')
  // StatsD server info
  private String statsdHost;
  private int statsdPort;
  // Current server info
  private String host;
  private String environment;
  private String server;

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getStatsdHost() {
    return statsdHost;
  }

  public void setStatsdHost(String statsdHost) {
    this.statsdHost = statsdHost;
  }

  public int getStatsdPort() {
    return statsdPort;
  }

  public void setStatsdPort(int statsdPort) {
    this.statsdPort = statsdPort;
  }

  @Override
  public void start() {
    if (isStarted()) {
      return;
    }

    try {
      host = host.replace(".", "-"); // replace periods in host with - so we don't create unnecessary dirs in graphite
      statsdPrefix = server + "." + environment.toLowerCase() + "." + host + ".app";
      statsd = new NonBlockingStatsDClient(statsdPrefix, statsdHost, statsdPort);
      started = true;
    } catch (Exception e) {
      addError("could not create statsd client", e);
    }
  }

  @Override
  public void stop() {
    if (!isStarted()) {
      return;
    }

    statsd.stop();
    statsd = null;
    started = false;
  }

  @Override
  protected void append(ILoggingEvent event) {
    statsd.incrementCounter(event.getLevel().toString());
  }
}
