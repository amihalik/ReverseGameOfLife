Reverse Game Of Life
=================

Pear Attack's submission for Kaggle's Reverse Game of Life (Ended Mar 2 2014)

https://www.kaggle.com/c/conway-s-reverse-game-of-life

Our solution had three phases: Prep, Simulation and Solve.

During the prep phase, we examined the end states of all of the games in the TEST set.  We recorded the "delta" value, and recorded a 7x7 "tile window" centered each cell on the board.  The combination of the delta value concatenated with a flattened version of the window served as the "tile key".  Therefore, for each game, we stored 400 tile keys.  The set of tile keys from the provided data (i.e. either the TRAIN or TEST file) is called the "Required Tiles".

During the simulation phase, we ran a large number of 20x20 Game Of Life simulations, recorded the start state for each cell in the game, and the 7x7 "tile window" center at that cell at a handful of "delta" steps in the simulation.  If this tile key appeared in the required tiles, we recorded the results.

During the solve phase, we went through each required tile and noted if, probabilistically from our simulation, we thought it started as "alive" or "dead".  If we did not see this tile key during our simulation, we assumed it started dead.

For our final solution, we ran approximately 200 million simulations.  During each simulation, we started with a random board, evolved the board five warm up steps (as specified in the Kaggle description), ran the game five more steps and sampled at each step.