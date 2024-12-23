import Canvas from './components/Canvas'

import {
  ResizableHandle,
  ResizablePanel,
  ResizablePanelGroup,
} from "../components/ui/resizable"

import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "../components/ui/card"



function App() {
  return (
    <><ResizablePanelGroup direction="horizontal">
    <ResizablePanel><Canvas></Canvas></ResizablePanel>
    <ResizableHandle />
    <ResizablePanel><Card>
  <CardHeader>
    <CardTitle>Card Title</CardTitle>
    <CardDescription>Card Description</CardDescription>
  </CardHeader>
  <CardContent>
    <p>Card Content</p>
  </CardContent>
  <CardFooter>
    <p>Card Footer</p>
  </CardFooter>
</Card>
</ResizablePanel>
    </ResizablePanelGroup>
    </>
  )
}

export default App
