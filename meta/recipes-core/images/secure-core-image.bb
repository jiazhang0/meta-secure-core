require secure-core-image.inc

IMAGE_INSTALL += "\
    util-linux \
"

inherit extrausers
# Set the root password to toor
EXTRA_USERS_PARAMS += "usermod -p '\$6\$cnS1MQVd07.jLdkj\$NgXwgR.LWtbi4qD3Drn6x0AQtVtX/CdfKPy1eJzuUC0YdD8s54fLAlsLm8vaoacAoqq6BHYGTAbUnAUNJKTOb1' root;"
