---
name: deploy-vercel
description: Standardize Vercel deployments for web apps and services, with emphasis on pre-deploy build validation, environment-variable coverage, preview checks, and production release verification. Use when Codex needs to prepare, review, troubleshoot, or execute a Vercel deployment; confirm build commands and project settings; audit missing or mismatched env vars across development, preview, and production; or verify that a production deployment is healthy after release.
---

# Deploy Vercel

Follow this workflow whenever shipping to Vercel from a local repo or CI pipeline.

## Workflow

1. Identify the deployment mode.
2. Confirm the project is linked to the intended Vercel project.
3. Validate build assumptions before deployment.
4. Check environment-variable coverage for the target environment.
5. Create a preview deployment when practical.
6. Verify the preview or production deployment with HTTP and log checks.
7. Promote or deploy to production only after the checks pass.

## 1. Establish Context

Collect the minimum context before touching deployment state:

- Framework and package manager
- Build command used locally and in Vercel
- Output directory or framework output assumptions
- Vercel project name, team scope, and target environment
- Whether the deploy is from source or `--prebuilt`
- Whether deployment protection, custom domains, or deployment checks are enabled

If the repo is not yet linked, use `vercel link`.

If the workflow depends on `vercel build` or `vercel dev`, refresh cached settings with `vercel pull` for the target environment.

## 2. Validate Build Inputs

Prefer catching build failures before deployment.

Build validation checklist:

- Confirm dependency install command matches the repo conventions.
- Confirm the Node.js or runtime version expected by the app and by Vercel project settings.
- Confirm monorepo root-directory assumptions if the app is not at repo root.
- Confirm the actual build command instead of assuming framework defaults.
- Run the local production build path when feasible.

Useful commands:

```bash
vercel pull --environment=production
vercel build --prod
```

Use `vercel build --target=preview` or `vercel build --target=<custom-environment>` when the deployment is not production.

If the repo already has a trusted build script and the user only wants diagnosis, compare local build output with the configured Vercel build behavior before changing settings.

## 3. Check Environment Variables

Treat env verification as a first-class gate. Missing env vars are a common cause of green local builds and broken remote deployments.

Audit env coverage in this order:

1. Find every required variable in the codebase, docs, and CI config.
2. Separate build-time variables from runtime-only variables.
3. Compare required variables against Vercel env assignments for `development`, `preview`, and `production`.
4. Check for branch-specific preview overrides when a branch deploy behaves differently.
5. Confirm secret values are not written into tracked files.

Useful commands:

```bash
vercel env ls
vercel env ls preview
vercel env ls production
vercel env pull .env.local
vercel env run -e production -- <build-or-test-command>
```

Notes:

- `vercel env pull <file>` is for writing environment variables into a local file for tools that expect one.
- `vercel pull` is for syncing project settings and cached env data under `.vercel/` for `vercel build` and `vercel dev`.
- Production env changes only affect new deployments, not previous ones.

When a required variable is absent, stop and report the exact variable name, target environment, and expected effect instead of continuing with a risky deploy.

## 4. Deploy Preview First

Unless the user explicitly wants a direct production release, prefer a preview deployment first.

Common commands:

```bash
vercel deploy
vercel inspect <deployment-url> --wait
```

Capture the deployment URL from `stdout`. Vercel documents that `stdout` of `vercel deploy` is always the deployment URL, so store it and reuse it in later verification steps.

If the team uses prebuilt deploys:

```bash
vercel build
vercel deploy --prebuilt
```

## 5. Verify the Deployment

Use direct checks, not just "deployment succeeded".

Minimum verification:

- Request `/` or a known health endpoint
- Check one route that exercises critical runtime config
- Inspect deployment status and metadata
- Check logs for errors or repeated failures

Useful commands:

```bash
vercel inspect <deployment-url> --wait
vercel curl / --deployment <deployment-url>
vercel logs --deployment <deployment-url> --level error
```

If deployment protection is enabled, prefer `vercel curl` because it handles protection bypass automatically. Note that the command is currently beta in Vercel CLI.

If verification depends on a specific API route, use the real endpoint rather than only checking `/`.

## 6. Release to Production

Use production deployment only after build and env checks are clean.

Typical command:

```bash
vercel deploy --prod
```

Then verify production immediately:

```bash
vercel inspect <production-deployment-url> --wait
vercel curl /
vercel logs --deployment <production-deployment-url> --level error
```

When the project uses custom domains, verify both:

- The deployment URL
- The production domain or domains expected to receive traffic

If the team relies on Vercel Deployment Checks, confirm they are satisfied before treating the release as complete.

## Failure Handling

If the deploy fails, classify the problem before proposing a fix:

- Build configuration mismatch
- Missing or wrong environment variable
- Wrong project linkage or team scope
- Framework output or root-directory mismatch
- Runtime error after successful build
- Domain, protection, or release-gating issue

Report the smallest actionable next step. Do not bundle unrelated fixes.

## Output Expectations

When helping with a deploy, produce a concise status summary:

- Target environment
- Build command used
- Env gaps found, if any
- Deployment URL
- Verification commands run
- Final outcome: blocked, preview-ready, or production-healthy

## Reference

Read [references/vercel-checklist.md](references/vercel-checklist.md) when you need a compact preflight and post-release checklist.
