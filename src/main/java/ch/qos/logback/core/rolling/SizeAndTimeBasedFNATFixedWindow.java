package ch.qos.logback.core.rolling;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.helper.SizeAndTimeBasedFixedWindowArchiveRemover;

@NoAutoStart
public class SizeAndTimeBasedFNATFixedWindow<E> extends SizeAndTimeBasedFNATP<E> {

  @Override
  public void start() {
    // we depend on certain fields having been initialized
    // in super.start()
    super.start();

    archiveRemover = new SizeAndTimeBasedFixedWindowArchiveRemover(tbrp.fileNamePattern, rc);
    archiveRemover.setContext(context);
  }
}
