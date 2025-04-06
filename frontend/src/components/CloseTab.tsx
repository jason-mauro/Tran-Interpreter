import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
    DialogClose,
    DialogFooter,
  } from "@/components/ui/dialog"
  import { X } from 'lucide-react'
  import React from "react"
  import { Button } from "./ui/button"
  
  interface CloseTabProps {
    tab: string;
    removeTab: (tab: string, event: React.MouseEvent) => void
  }
  
  const CloseTab: React.FC<CloseTabProps> = ({ tab, removeTab }) => {
    return (
      <Dialog>
        <DialogTrigger asChild>
          <button className="ml-2 p-0.5 rounded-sm hover:bg-accent text-card-foreground hover:text-accent-foreground">
              <X size={14} />
          </button>
        </DialogTrigger>
          <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Delete File?</DialogTitle>
            <DialogDescription>
              This will permanently delete "{tab}". Are you sure you want to continue?
            </DialogDescription>
          </DialogHeader>
            <div className="grid flex-1 gap-2">
          
              </div>
          <DialogFooter className="sm:justify-start">
            <DialogClose asChild>
              <Button type="button" variant="destructive" onClick={(e) => removeTab(tab, e)}>
                Delete
              </Button>
            </DialogClose>
            <DialogClose asChild>
              <Button type="button" variant="outline">
                Cancel
              </Button>
            </DialogClose>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    )
  };
  
  export default CloseTab