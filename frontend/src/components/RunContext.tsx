"use client"

import * as React from "react"
import { Check, ChevronsUpDown } from "lucide-react"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandItem,
  CommandList,
} from "@/components/ui/command"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"


const context = [
    {
      value: "current",
      label: "Run current file",
    },
    {
      value: "project",
      label: "Run as project",
    }
  ];


    interface ThemePickerProps {
        setContext : React.Dispatch<React.SetStateAction<string>>;
    }


const RunContext: React.FC<ThemePickerProps> = ({ setContext }) => {
  const [open, setOpen] = React.useState(false)
  const [value, setValue] = React.useState("current")

  React.useEffect(() => {
    setContext(value)
  }, [value])

  return (
    <div className="flex-col items-center ml-auto">

        <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
            <Button
            variant="outline"
            role="combobox"
            aria-expanded={open}
            className="w-[175px] justify-between"
            >
            {context.find((context) => context.value === value)?.label}
            <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
            </Button>
        </PopoverTrigger>
        <PopoverContent className="w-[175px] p-0">
            <Command>
            <CommandList>
                <CommandEmpty>No framework found.</CommandEmpty>
                <CommandGroup>
                {context.map((context) => (
                    <CommandItem
                    key={context.value}
                    value={context.value}
                    onSelect={(currentValue) => {
                        setValue(currentValue === value ? "" : currentValue)
                        setOpen(false)
                    }}
                    >
                    <Check
                        className={cn(
                        "mr-2 h-4 w-4",
                        value === context.value ? "opacity-100" : "opacity-0"
                        )}
                    />
                    {context.label}
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

export default RunContext;