"use client"

import * as React from "react"
import { Check, ChevronsUpDown } from "lucide-react"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"

const themes = [
    {
      value: "Active4D",
      label: "Active4D",
    },
    {
      value: "All Hallows Eve",
      label: "All Hallows Eve",
    },
    {
      value: "Amy",
      label: "Amy",
    },
    {
      value: "Birds of Paradise",
      label: "Birds of Paradise",
    },
    {
      value: "Blackboard",
      label: "Blackboard",
    },
    {
      value: "Brilliance Black",
      label: "Brilliance Black",
    },
    {
      value: "Brilliance Dull",
      label: "Brilliance Dull",
    },
    {
      value: "Chrome DevTools",
      label: "Chrome DevTools",
    },
    {
      value: "Clouds Midnight",
      label: "Clouds Midnight",
    },
    {
      value: "Clouds",
      label: "Clouds",
    },
    {
      value: "Cobalt",
      label: "Cobalt",
    },
    {
      value: "Cobalt2",
      label: "Cobalt2",
    },
    {
      value: "Dawn",
      label: "Dawn",
    },
    {
      value: "Dominion Day",
      label: "Dominion Day",
    },
    {
      value: "Dracula",
      label: "Dracula",
    },
    {
      value: "Dreamweaver",
      label: "Dreamweaver",
    },
    {
      value: "Eiffel",
      label: "Eiffel",
    },
    {
      value: "Espresso Libre",
      label: "Espresso Libre",
    },
    {
      value: "GitHub Dark",
      label: "GitHub Dark",
    },
    {
      value: "GitHub Light",
      label: "GitHub Light",
    },
    {
      value: "GitHub",
      label: "GitHub",
    },
    {
      value: "IDLE",
      label: "IDLE",
    },
    {
      value: "Katzenmilch",
      label: "Katzenmilch",
    },
    {
      value: "Kuroir Theme",
      label: "Kuroir Theme",
    },
    {
      value: "LAZY",
      label: "LAZY",
    },
    {
      value: "MagicWB (Amiga)",
      label: "MagicWB (Amiga)",
    },
    {
      value: "Merbivore Soft",
      label: "Merbivore Soft",
    },
    {
      value: "Merbivore",
      label: "Merbivore",
    },
    {
      value: "Monokai Bright",
      label: "Monokai Bright",
    },
    {
      value: "Monokai",
      label: "Monokai",
    },
    {
      value: "Night Owl",
      label: "Night Owl",
    },
    {
      value: "Nord",
      label: "Nord",
    },
    {
      value: "Oceanic Next",
      label: "Oceanic Next",
    },
    {
      value: "Pastels on Dark",
      label: "Pastels on Dark",
    },
    {
      value: "Slush and Poppies",
      label: "Slush and Poppies",
    },
    {
      value: "Solarized-dark",
      label: "Solarized-dark",
    },
    {
      value: "Solarized-light",
      label: "Solarized-light",
    },
    {
      value: "SpaceCadet",
      label: "SpaceCadet",
    },
    {
      value: "Sunburst",
      label: "Sunburst",
    },
    {
      value: "Textmate (Mac Classic)",
      label: "Textmate (Mac Classic)",
    },
    {
      value: "Tomorrow-Night-Blue",
      label: "Tomorrow-Night-Blue",
    },
    {
      value: "Tomorrow-Night-Bright",
      label: "Tomorrow-Night-Bright",
    },
    {
      value: "Tomorrow-Night-Eighties",
      label: "Tomorrow-Night-Eighties",
    },
    {
      value: "Tomorrow-Night",
      label: "Tomorrow-Night",
    },
    {
      value: "Tomorrow",
      label: "Tomorrow",
    },
    {
      value: "Twilight",
      label: "Twilight",
    },
    {
      value: "Upstream Sunburst",
      label: "Upstream Sunburst",
    },
    {
      value: "Vibrant Ink",
      label: "Vibrant Ink",
    },
    {
      value: "Xcode_default",
      label: "Xcode_default",
    },
    {
      value: "Zenburnesque",
      label: "Zenburnesque",
    },
    {
      value: "iPlastic",
      label: "iPlastic",
    },
    {
      value: "idleFingers",
      label: "idleFingers",
    },
    {
      value: "krTheme",
      label: "krTheme",
    },
    {
      value: "monoindustrial",
      label: "monoindustrial",
    },
  ];


    interface ThemePickerProps {
        onThemeChange?: (theme: string) => void
    }


const ThemePicker: React.FC<ThemePickerProps> = ({ onThemeChange }) => {
  const [open, setOpen] = React.useState(false)
  const [value, setValue] = React.useState("GitHub Light")

  React.useEffect(() => {
    onThemeChange?.(value)
  }, [value, onThemeChange])

  return (
    <div className="flex-col items-center">
        <p className="text-sm text-muted-foreground">Theme</p>
        <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
            <Button
            variant="outline"
            role="combobox"
            aria-expanded={open}
            className="w-[200px] justify-between"
            >
            {value
                ? themes.find((theme) => theme.value === value)?.label
                : "Select Theme..."}
            <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
            </Button>
        </PopoverTrigger>
        <PopoverContent className="w-[200px] p-0">
            <Command>
            <CommandInput placeholder="Search Themes..." />
            <CommandList>
                <CommandEmpty>No framework found.</CommandEmpty>
                <CommandGroup>
                {themes.map((theme) => (
                    <CommandItem
                    key={theme.value}
                    value={theme.value}
                    onSelect={(currentValue) => {
                        setValue(currentValue === value ? "" : currentValue)
                        setOpen(false)
                    }}
                    >
                    <Check
                        className={cn(
                        "mr-2 h-4 w-4",
                        value === theme.value ? "opacity-100" : "opacity-0"
                        )}
                    />
                    {theme.label}
                    </CommandItem>
                ))}
                </CommandGroup>
            </CommandList>
            </Command>
        </PopoverContent>
        </Popover>
    </div>
  )
}

export default ThemePicker;