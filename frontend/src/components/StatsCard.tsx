import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"

const StatsCard = () => {
  return (
    <div className="p-4 space-y-4">
      {/* Primary color card */}
      <Card className="bg-primary text-primary-foreground">
        <CardHeader>
          <CardTitle>Primary Theme Card</CardTitle>
        </CardHeader>
        <CardContent>
          Content with primary theme
        </CardContent>
      </Card>

      {/* Custom chart colors card */}
      <div className="grid grid-cols-5 gap-4">
        <div className="h-20 rounded-lg" style={{ backgroundColor: "hsl(var(--chart-1))" }}></div>
        <div className="h-20 rounded-lg" style={{ backgroundColor: "hsl(var(--chart-2))" }}></div>
        <div className="h-20 rounded-lg" style={{ backgroundColor: "hsl(var(--chart-3))" }}></div>
        <div className="h-20 rounded-lg" style={{ backgroundColor: "hsl(var(--chart-4))" }}></div>
        <div className="h-20 rounded-lg" style={{ backgroundColor: "hsl(var(--chart-5))" }}></div>
      </div>

      {/* Secondary color card with accent border */}
      <Card className="bg-secondary text-secondary-foreground border-2 border-accent">
        <CardHeader>
          <CardTitle>Secondary Theme Card</CardTitle>
        </CardHeader>
        <CardContent>
          Content with secondary theme and accent border
        </CardContent>
      </Card>
    </div>
  )
}

export default StatsCard