# Vercel Deployment Checklist

## Preflight

- Confirm the repo is linked to the intended Vercel project.
- Confirm framework, root directory, install command, and build command.
- Confirm runtime version expectations.
- Run `vercel pull --environment=<target>` when using `vercel build` or `vercel dev`.
- Run a local build path when feasible.
- List required env vars from code and config.
- Compare required env vars against `vercel env ls <target>`.

## Preview

- Run `vercel deploy`.
- Save the deployment URL emitted on stdout.
- Wait for completion with `vercel inspect <deployment-url> --wait`.
- Check `/` or a health route with `vercel curl / --deployment <deployment-url>`.
- Check runtime errors with `vercel logs --deployment <deployment-url> --level error`.

## Production

- Run `vercel deploy --prod`.
- Wait for completion with `vercel inspect <deployment-url> --wait`.
- Verify the deployment URL.
- Verify the production domain.
- Check logs for errors after release.

## Blocking Conditions

- Missing production env var
- Unknown build command or root directory
- Failing local or Vercel build
- Deployment-check gate not satisfied
- Runtime errors on critical routes
