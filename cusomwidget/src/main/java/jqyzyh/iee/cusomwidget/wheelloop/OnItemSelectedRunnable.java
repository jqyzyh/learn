package jqyzyh.iee.cusomwidget.wheelloop;

/**
 * @author jqyzyh
 *
 */
final class OnItemSelectedRunnable implements Runnable {
    final LoopView loopView;

    OnItemSelectedRunnable(LoopView loopview) {
        loopView = loopview;
    }

    @Override
    public final void run() {
        loopView.onItemSelectedListener.onItemSelected(loopView.getmSelectedItem());
    }
}
