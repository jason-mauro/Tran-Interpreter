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

const keybinds = [
    {
      value: "Default",
      label: "Default",
    },
    {
      value: "Vim",
      label: "Vim",
    },
    {
      value: "Emacs",
      label: "Emacs",
    }
  ];


    interface ThemePickerProps {
        onKeybindChange?: (keybinds: string) => void
    }


const KeybindPicker: React.FC<ThemePickerProps> = ({ onKeybindChange }) => {
  const [open, setOpen] = React.useState(false)
  const [value, setValue] = React.useState("Default")

  React.useEffect(() => {
    onKeybindChange?.(value)
  }, [value, onKeybindChange])

  return (
    <div className="flex-col items-center">
        <p className="text-sm text-muted-foreground">Keybinds</p>
        <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
            <Button
            variant="outline"
            role="combobox"
            aria-expanded={open}
            className="w-[200px] justify-between"
            >
            {value
                ? keybinds.find((keybind) => keybind.value === value)?.label
                : "Select Keybindings"}
            <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
            </Button>
        </PopoverTrigger>
        <PopoverContent className="w-[200px] p-0">
            <Command>
            <CommandList>
                <CommandEmpty>No framework found.</CommandEmpty>
                <CommandGroup>
                {keybinds.map((theme) => (
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

export default KeybindPicker;