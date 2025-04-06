"use client"

import * as React from "react"
import { createContext, useContext, useEffect, useState } from "react"

type Theme = "dark" | "light" | "system"

type ThemeProviderProps = {
  children: React.ReactNode
  defaultTheme?: Theme
  storageKey?: string
}

type ThemeProviderState = {
  theme: Theme
  setTheme: (theme: Theme) => void
}

const initialState: ThemeProviderState = {
  theme: "system",
  setTheme: () => null,
}

const ThemeProviderContext = createContext<ThemeProviderState>(initialState)

const isClient = typeof window !== 'undefined'

const getSystemTheme = (): "light" | "dark" => {
  if (!isClient) return "light"
  return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light"
}

function ThemeProvider({
  children,
  defaultTheme = "system",
  storageKey = "vite-ui-theme",
  ...props
}: ThemeProviderProps) {
  const [theme, setTheme] = useState<Theme>(() => {
    if (!isClient) return defaultTheme
    return (localStorage.getItem(storageKey) as Theme) || defaultTheme
  })

  // Apply theme immediately on mount
  useEffect(() => {
    const root = window.document.documentElement
    root.classList.remove("light", "dark")

    if (theme === "system") {
      const systemTheme = getSystemTheme()
      root.classList.add(systemTheme)
    } else {
      root.classList.add(theme)
    }
  }, [])

  useEffect(() => {
    if (!isClient) return

    const applySystemTheme = () => {
      const systemTheme = getSystemTheme()
      const root = window.document.documentElement
      root.classList.remove("light", "dark")
      root.classList.add(systemTheme)
    }

    if (theme === "system") {
      applySystemTheme()
      
      const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)")
      const handleChange = () => applySystemTheme()
      
      mediaQuery.addEventListener("change", handleChange)
      return () => mediaQuery.removeEventListener("change", handleChange)
    } else {
      const root = window.document.documentElement
      root.classList.remove("light", "dark")
      root.classList.add(theme)
    }
  }, [theme])

  const value = {
    theme,
    setTheme: (newTheme: Theme) => {
      if (isClient) {
        localStorage.setItem(storageKey, newTheme)
      }
      setTheme(newTheme)
    },
  }

  return (
    <ThemeProviderContext.Provider value={value} {...props}>
      {children}
    </ThemeProviderContext.Provider>
  )
}

function useTheme() {
  const context = useContext(ThemeProviderContext)

  if (context === undefined)
    throw new Error("useTheme must be used within a ThemeProvider")

  return context
}

export { ThemeProvider, useTheme }