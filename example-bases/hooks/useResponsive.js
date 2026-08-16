import { useEffect, useMemo, useState } from "react";

const DEFAULT_BP = { sm: 576, md: 768, lg: 1024, xl: 1280, xxl: 1536 };

function sortEntries(obj) {
  return Object.entries(obj).sort((a, b) => a[1] - b[1]);
}
function computeBpName(width, sortedBps) {
  let name = "xs";
  for (const [n, px] of sortedBps) if (width >= px) name = n;
  return name;
}

function rafThrottle(fn) {
  let raf = null;
  return (...args) => {
    if (raf) return;
    raf = requestAnimationFrame(() => {
      fn(...args);
      raf = null;
    });
  };
}

export default function useResponsive(customBreakpoints) {
  const breakpoints = useMemo(
    () => ({ ...DEFAULT_BP, ...(customBreakpoints || {}) }),
    [customBreakpoints]
  );
  const sorted = useMemo(() => sortEntries(breakpoints), [breakpoints]);

  const isBrowser = typeof window !== "undefined";
  const [size, setSize] = useState(() => ({
    width: isBrowser ? window.innerWidth : 0,
    height: isBrowser ? window.innerHeight : 0,
  }));

  useEffect(() => {
    if (!isBrowser) return;
    const handler = rafThrottle(() => {
      setSize({ width: window.innerWidth, height: window.innerHeight });
    });
    window.addEventListener("resize", handler);
    return () => window.removeEventListener("resize", handler);
  }, [isBrowser]);

  const bpName = useMemo(() => computeBpName(size.width, sorted), [size.width, sorted]);

  // Yardımcılar
  const up = (bp) => size.width >= (breakpoints[bp] ?? 0);
  const down = (bp) => size.width < (breakpoints[bp] ?? Number.MAX_SAFE_INTEGER);
  const between = (minBp, maxBp) =>
    size.width >= (breakpoints[minBp] ?? 0) &&
    size.width < (breakpoints[maxBp] ?? Number.MAX_SAFE_INTEGER);

  return {
    width: size.width,
    height: size.height,
    bpName,           // "xs" | "sm" | "md" | "lg" | "xl" | "xxl"
    up, down, between,
    breakpoints,
  };
}
