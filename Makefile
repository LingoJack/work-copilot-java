SHELL := /bin/bash

.PHONY: \

current_dir:
	@echo -----------------------------
	@echo "current dir:"
	@echo "    $$(pwd)"
	@echo -----------------------------
	@echo

push: current_dir
	@git add . && git status && (git commit -m '优化功能 & 修复了一些已知的bug' || exit 0) && git push origin main

pull: current_dir
	@git pull origin main

