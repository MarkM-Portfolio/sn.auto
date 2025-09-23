SRCDIR=../../LC4.0-GUI-BVT
#rsync -avz --dry-run --delete $SRCDIR/src bvt.gui
rsync -avz --delete $SRCDIR/src ${SRCDIR}/test_config bvt.gui
rsync -avz --delete --exclude chromedriver --exclude CVS --delete-excluded  ${SRCDIR}/resources bvt.gui
