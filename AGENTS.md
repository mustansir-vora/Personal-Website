<!-- BEGIN:nextjs-agent-rules -->
# This is NOT the Next.js you know

This version has breaking changes — APIs, conventions, and file structure may all differ from your training data. Read the relevant guide in `node_modules/next/dist/docs/` before writing any code. Heed deprecation notices.
<!-- END:nextjs-agent-rules -->

# Project Context

This is a personal portfolio website built with Next.js.
- **Static Export**: The app is configured for static export (`output: 'export'` in `next.config.ts`). **CRITICAL: You cannot use features that require a Node.js server (like Server Actions, dynamic API routes, or `next/image` optimization without `unoptimized: true`).**
- **Routing**: Next.js App Router (`src/app`).
- **Styling**: Tailwind CSS v4. Do not create custom CSS files; use Tailwind utility classes.
- **Animations**: Framer Motion.
- **Icons**: Lucide React.
- **File Structure**:
  - `src/components/ui`: Primitive, reusable components.
  - `src/components/sections`: Larger page blocks.
  - `src/lib`: Helper functions and hooks.
- **Guidelines**: Please refer to `CLAUDE.md` for comprehensive design, layout, and animation guidelines.

# AI Assistant Behavioral Rules
1. **No Destructive Operations**: Never run `rm -rf` or delete files outside the workspace.
2. **Consult `CLAUDE.md` First**: Before making UI or architectural decisions, always align with the aesthetics defined in `CLAUDE.md`.
3. **Use Types**: We are using TypeScript. Ensure strict typing for props and avoid using `any`.
4. **Mobile-First**: Always verify how layout changes affect small screens (e.g., flex directions, padding).
