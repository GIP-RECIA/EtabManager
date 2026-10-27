SHELL := /bin/bash

help default:
	@echo "📦 Available commands"
	@echo
	@echo " init                             > Initialize project"
	@echo " clean                            > Clean project"
	@echo " dedupe                           > De-duplicate yarn dependencies"
	@echo " license                          > Generate missing licenses"
	@echo " [frontend|backend]               > Launch"
	@echo " [package|snapshot|release]       > Build"
	@echo " changelog                        > Generate CHANGELOG"

.PHONY: \
	help \
	default \
	git-status-ok \
	check-main \
	init \
	clean \
	dedupe \
	license \
	license-check \
	license-generate \
	license-commit \
	frontend \
	backend \
	package \
	snapshot \
	release \
	changelog

# Utilities

git-status-ok:
	@if ! git diff --quiet || ! git diff --cached --quiet; then \
		echo "❌ Tracked files have been modified (working tree or index)"; \
		exit 1; \
	fi

check-main:
	@if ! git symbolic-ref --quiet --short HEAD | grep -qx main; then \
		echo "❌ You are not on main branch"; \
		exit 1; \
	fi

# Commands

init:
	@./scripts/init.sh

clean:
	@./mvnw clean
	@find . \
	-name node_modules -type d -prune -or \
	-name cache -type d -or \
	-name dist -type d -or \
	-name playwright-report -type d -or \
	-name test-results -type d -or \
	-name results.json -type f -or \
	-name '*.bak' -type f -or \
	-name '*.log' -type f -or \
	-name '*.log.*.gz' -type f | \
	xargs rm -rf

dedupe: git-status-ok
	@yarn dedupe && \
	if [ -n "$$(git status --porcelain yarn.lock)" ]; then \
		echo "📦 yarn.lock has changed → auto commit"; \
		git add yarn.lock && \
		git commit -m "build(yarn): de-duplicate entries" && \
		git push; \
	else \
		echo "✅ yarn.lock hasn't changed"; \
	fi

license: git-status-ok license-generate license-commit

license-check:
	@./scripts/license.sh 1

license-generate:
	@./scripts/license.sh 2

license-commit:
	@./scripts/license.sh 3

frontend:
	@yarn dev

backend:
	@./mvnw clean spring-boot:run -Pdev

package:
	@./scripts/build.sh 0

snapshot:
	@./scripts/build.sh 1

release: check-main
	@./scripts/build.sh 2

changelog: git-status-ok check-main
	@yarn changelog:generate
