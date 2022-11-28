package(default_visibility = ["//visibility:public"])

py_binary(
  name = "configure",
  srcs = ["configure.py"],
  deps = [":common", ":workspace_path_finder"],
  imports = [":common", ":workspace_path_finder"],
  main = "configure.py"
)

py_library(
  name = "common",
  srcs = ["common.py"],
  deps = [],
  imports = []
)

py_library(
  name = "workspace_path_finder",
  srcs = ["workspace_path_finder.py"],
  deps = [],
  imports = []
)